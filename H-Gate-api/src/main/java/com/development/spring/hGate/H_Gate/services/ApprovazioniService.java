package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.approvazioni.ApprovazioniConteggioDTO;
import com.development.spring.hGate.H_Gate.dtos.approvazioni.MedicoInAttesaDTO;
import com.development.spring.hGate.H_Gate.dtos.approvazioni.RichiestaAssociazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PazienteTutore;
import com.development.spring.hGate.H_Gate.entity.RichiestaAssociazione;
import com.development.spring.hGate.H_Gate.entity.TutoreLegale;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.enums.StatoRichiestaEnum;
import com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteTutoreRepository;
import com.development.spring.hGate.H_Gate.repositories.RichiestaAssociazioneRepository;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.development.spring.hGate.H_Gate.shared.utils.AfterCommit;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Approvazioni gestite dall'amministratore:
 * <ul>
 *   <li>medici che si sono registrati da soli (non visibili né prenotabili finché non approvati);</li>
 *   <li>tutori che chiedono di essere associati a un minore già registrato.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ApprovazioniService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovazioniService.class);

    private final MedicoRepository medicoRepository;
    private final UserRepository userRepository;
    private final RichiestaAssociazioneRepository richiestaRepository;
    private final PazienteTutoreRepository pazienteTutoreRepository;
    private final NotificheService notificheService;

    // ================================================================ MEDICI

    @Transactional(readOnly = true)
    public List<MedicoInAttesaDTO> mediciInAttesa() {
        return medicoRepository.findMediciDaVerificare().stream().map(this::toDTO).toList();
    }

    @Transactional
    public void approvaMedico(Integer medicoId, Integer adminUserId) {
        Medico medico = findMedicoInAttesa(medicoId);
        Users user = medico.getUser();
        user.setIsVerified(true);
        userRepository.save(user);
        logger.info("Medico {} approvato dall'admin {}", medicoId, adminUserId);

        // Email: il medico non può ancora accedere, quindi la notifica in-app non basterebbe
        AfterCommit.run(() -> notificheService.creaNotifica(user, TipoNotificaEnum.SISTEMA,
                "Account approvato",
                "Il tuo account H-Gate è stato approvato dalla clinica. Ora puoi accedere e gestire la tua agenda.",
                null, true));
    }

    @Transactional
    public void rifiutaMedico(Integer medicoId, Integer adminUserId, String motivo) {
        Medico medico = findMedicoInAttesa(medicoId);
        Users user = medico.getUser();
        // L'account viene disattivato (non cancellato) per conservare traccia della richiesta
        user.setIsActive(false);
        userRepository.save(user);
        logger.info("Medico {} rifiutato dall'admin {}", medicoId, adminUserId);

        AfterCommit.run(() -> notificheService.creaNotifica(user, TipoNotificaEnum.SISTEMA,
                "Registrazione non approvata",
                "La tua richiesta di registrazione come specialista non è stata approvata."
                        + motivoTesto(motivo) + " Per informazioni contatta la segreteria della clinica.",
                null, true));
    }

    private Medico findMedicoInAttesa(Integer medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medico non trovato"));
        Users user = medico.getUser();
        if (Boolean.TRUE.equals(user.getIsVerified()) || Boolean.FALSE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Questa richiesta è già stata gestita");
        }
        return medico;
    }

    // =========================================================== ASSOCIAZIONI

    /**
     * Crea (o restituisce, se esiste già) una richiesta di associazione in attesa
     * e avvisa gli amministratori.
     */
    @Transactional
    public RichiestaAssociazione creaRichiestaAssociazione(Paziente paziente, TutoreLegale tutore, String relazione) {
        if (relazione == null || relazione.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La relazione con il minore è obbligatoria");
        }
        var esistente = richiestaRepository.findFirstByPazienteIdAndTutoreIdAndStato(
                paziente.getId(), tutore.getId(), StatoRichiestaEnum.IN_ATTESA);
        if (esistente.isPresent()) {
            return esistente.get();
        }

        RichiestaAssociazione richiesta = richiestaRepository.save(RichiestaAssociazione.builder()
                .paziente(paziente)
                .tutore(tutore)
                .relazione(relazione.trim().toUpperCase())
                .stato(StatoRichiestaEnum.IN_ATTESA)
                .createdAt(LocalDateTime.now())
                .build());

        String richiedente = tutore.getUser().getNome() + " " + tutore.getUser().getCognome();
        for (Users admin : userRepository.findActiveByRole(Role.ADMIN)) {
            AfterCommit.run(() -> notificheService.creaNotifica(admin, TipoNotificaEnum.SISTEMA,
                    "Nuova richiesta di associazione",
                    richiedente + " chiede di essere associato come tutore a un minore già registrato.",
                    "/approvazioni", false));
        }
        return richiesta;
    }

    @Transactional(readOnly = true)
    public List<RichiestaAssociazioneDTO> richieste(StatoRichiestaEnum stato) {
        return richiestaRepository.findByStatoOrderByCreatedAtAsc(stato).stream().map(this::toDTO).toList();
    }

    @Transactional
    public void approvaAssociazione(Integer richiestaId, Integer adminUserId) {
        RichiestaAssociazione richiesta = findRichiestaInAttesa(richiestaId);

        PazienteTutoreId id = new PazienteTutoreId();
        id.setPazienteId(richiesta.getPaziente().getId());
        id.setTutoreId(richiesta.getTutore().getId());
        if (!pazienteTutoreRepository.existsById(id)) {
            PazienteTutore relazione = new PazienteTutore();
            relazione.setId(id);
            relazione.setPaziente(richiesta.getPaziente());
            relazione.setTutore(richiesta.getTutore());
            relazione.setRelazione(richiesta.getRelazione());
            pazienteTutoreRepository.save(relazione);
        }

        chiudi(richiesta, StatoRichiestaEnum.APPROVATA, adminUserId, null);
        logger.info("Richiesta associazione {} approvata dall'admin {}", richiestaId, adminUserId);

        AfterCommit.run(() -> notificheService.creaNotifica(richiesta.getTutore().getUser(), TipoNotificaEnum.SISTEMA,
                "Associazione approvata",
                "La clinica ha verificato la tua richiesta: ora puoi gestire le visite di "
                        + richiesta.getPaziente().getNome() + " " + richiesta.getPaziente().getCognome() + ".",
                null, true));
    }

    @Transactional
    public void rifiutaAssociazione(Integer richiestaId, Integer adminUserId, String motivo) {
        RichiestaAssociazione richiesta = findRichiestaInAttesa(richiestaId);
        chiudi(richiesta, StatoRichiestaEnum.RIFIUTATA, adminUserId, motivo);
        logger.info("Richiesta associazione {} rifiutata dall'admin {}", richiestaId, adminUserId);

        AfterCommit.run(() -> notificheService.creaNotifica(richiesta.getTutore().getUser(), TipoNotificaEnum.SISTEMA,
                "Associazione non approvata",
                "La tua richiesta di associazione al minore non è stata approvata." + motivoTesto(motivo)
                        + " Per informazioni contatta la segreteria della clinica.",
                null, true));
    }

    private RichiestaAssociazione findRichiestaInAttesa(Integer richiestaId) {
        RichiestaAssociazione richiesta = richiestaRepository.findById(richiestaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Richiesta non trovata"));
        if (richiesta.getStato() != StatoRichiestaEnum.IN_ATTESA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Questa richiesta è già stata gestita");
        }
        return richiesta;
    }

    private void chiudi(RichiestaAssociazione richiesta, StatoRichiestaEnum stato, Integer adminUserId, String motivo) {
        richiesta.setStato(stato);
        richiesta.setGestitaDa(userRepository.findById(adminUserId).orElse(null));
        richiesta.setDataGestione(LocalDateTime.now());
        richiesta.setMotivoRifiuto(motivo == null || motivo.isBlank() ? null : motivo.trim());
        richiestaRepository.save(richiesta);
    }

    // ============================================================== CONTEGGI

    @Transactional(readOnly = true)
    public ApprovazioniConteggioDTO conteggio() {
        return new ApprovazioniConteggioDTO(
                medicoRepository.findMediciDaVerificare().size(),
                richiestaRepository.countByStato(StatoRichiestaEnum.IN_ATTESA));
    }

    // =============================================================== MAPPING

    private MedicoInAttesaDTO toDTO(Medico m) {
        Users u = m.getUser();
        return MedicoInAttesaDTO.builder()
                .medicoId(m.getId())
                .userId(u.getId())
                .nome(u.getNome())
                .cognome(u.getCognome())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .specializzazione(m.getSpecializzazione())
                .numeroAlbo(m.getNumeroAlbo())
                .universita(m.getUniversita())
                .annoLaurea(m.getAnnoLaurea())
                .dataRegistrazione(u.getCreatedAt())
                .build();
    }

    private RichiestaAssociazioneDTO toDTO(RichiestaAssociazione r) {
        Users t = r.getTutore().getUser();
        Paziente p = r.getPaziente();
        List<String> tutoriAttuali = pazienteTutoreRepository.findAllByPazienteIdWithTutore(p.getId()).stream()
                .map(pt -> pt.getTutore().getUser().getNome() + " " + pt.getTutore().getUser().getCognome()
                        + " (" + pt.getRelazione() + ")")
                .toList();
        return RichiestaAssociazioneDTO.builder()
                .id(r.getId())
                .stato(r.getStato())
                .relazione(r.getRelazione())
                .createdAt(r.getCreatedAt())
                .dataGestione(r.getDataGestione())
                .motivoRifiuto(r.getMotivoRifiuto())
                .tutoreUserId(t.getId())
                .tutoreNome(t.getNome())
                .tutoreCognome(t.getCognome())
                .tutoreEmail(t.getEmail())
                .tutoreTelefono(t.getTelefono())
                .pazienteId(p.getId())
                .pazienteNome(p.getNome())
                .pazienteCognome(p.getCognome())
                .pazienteCodiceFiscale(p.getCodiceFiscale())
                .pazienteDataNascita(p.getDataNascita())
                .tutoriAttuali(tutoriAttuali)
                .build();
    }

    private String motivoTesto(String motivo) {
        return motivo == null || motivo.isBlank() ? "" : " Motivo: " + motivo.trim() + ".";
    }
}
