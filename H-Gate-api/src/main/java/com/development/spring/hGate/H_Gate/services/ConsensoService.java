package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.consensi.ConsensoDTO;
import com.development.spring.hGate.H_Gate.dtos.consensi.StatoConsensoDTO;
import com.development.spring.hGate.H_Gate.entity.Consenso;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.enums.TipoConsensoEnum;
import com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum;
import com.development.spring.hGate.H_Gate.repositories.ConsensoRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.development.spring.hGate.H_Gate.shared.utils.AfterCommit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Consensi al trattamento dei dati del minore. Ogni concessione/revoca è una nuova riga dello
 * storico, con versione dell'informativa, autore, data e IP. Il campo pazienti.consenso_privacy
 * resta allineato all'ultimo evento per compatibilità con il codice e le viste esistenti.
 */
@Service
@RequiredArgsConstructor
public class ConsensoService {

    private static final Logger logger = LoggerFactory.getLogger(ConsensoService.class);

    public static final String MSG_CONSENSO_OBBLIGATORIO =
            "Per registrare il minore è necessario prendere visione dell'informativa e prestare il consenso al trattamento dei dati.";
    public static final String MSG_CONSENSO_REVOCATO =
            "Il consenso al trattamento dei dati di questo minore è stato revocato: non è possibile prenotare nuove visite. "
            + "Puoi prestare di nuovo il consenso dalla sezione Privacy e consensi.";

    private final ConsensoRepository consensoRepository;
    private final PazienteRepository pazienteRepository;
    private final UserRepository userRepository;
    private final NotificheService notificheService;

    @Value("${application.privacy.versione-informativa:1.0}")
    private String versioneInformativa;

    public String getVersioneInformativa() {
        return versioneInformativa;
    }

    @Transactional
    public Consenso concedi(Paziente paziente, Users autore, String ip) {
        return registra(paziente, autore, true, ip, null);
    }

    @Transactional
    public Consenso concedi(Integer pazienteId, Integer autoreUserId, String ip) {
        return concedi(paziente(pazienteId), utente(autoreUserId), ip);
    }

    @Transactional
    public Consenso revoca(Integer pazienteId, Integer autoreUserId, String ip, String motivo) {
        Paziente paziente = paziente(pazienteId);
        Users autore = utente(autoreUserId);
        if (!attivo(paziente)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Il consenso risulta già revocato");
        }
        Consenso c = registra(paziente, autore, false, ip, motivo);

        String testo = autore.getNome() + " " + autore.getCognome() + " ha revocato il consenso al trattamento dei dati di "
                + paziente.getNome() + " " + paziente.getCognome() + ".";
        for (Users admin : userRepository.findActiveByRole(Role.ADMIN)) {
            AfterCommit.run(() -> notificheService.creaNotifica(admin, TipoNotificaEnum.SISTEMA,
                    "Consenso privacy revocato", testo, null, false));
        }
        return c;
    }

    /** Si possono prenotare visite solo con un consenso attivo. */
    @Transactional(readOnly = true)
    public void verificaConsensoPerPrenotazione(Paziente paziente) {
        if (!attivo(paziente)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_CONSENSO_REVOCATO);
        }
    }

    @Transactional(readOnly = true)
    public StatoConsensoDTO stato(Integer pazienteId, boolean conStorico) {
        Paziente p = paziente(pazienteId);
        Optional<Consenso> ultimo = ultimo(p.getId());
        boolean attivo = attivo(p);
        String versione = ultimo.map(Consenso::getVersioneInformativa).orElse(null);
        return StatoConsensoDTO.builder()
                .pazienteId(p.getId())
                .pazienteNome(p.getNome())
                .pazienteCognome(p.getCognome())
                .attivo(attivo)
                .aggiornato(attivo && versioneInformativa.equals(versione))
                .versioneConsenso(versione)
                .versioneCorrente(versioneInformativa)
                .dataEvento(ultimo.map(Consenso::getDataEvento).orElse(p.getDataConsenso()))
                .espressoDa(ultimo.map(c -> nome(c.getUser())).orElse(null))
                .storico(conStorico ? consensoRepository.storico(p.getId()).stream().map(this::toDTO).toList() : null)
                .build();
    }

    @Transactional(readOnly = true)
    public List<StatoConsensoDTO> statoMinoriDelTutore(Integer tutoreUserId) {
        return pazienteRepository.findByUserId(tutoreUserId).stream()
                .map(p -> stato(p.getId(), false))
                .toList();
    }

    // ----------------------------------------------------------------------

    private Consenso registra(Paziente paziente, Users autore, boolean concesso, String ip, String note) {
        LocalDateTime ora = LocalDateTime.now();
        Consenso c = consensoRepository.save(Consenso.builder()
                .paziente(paziente)
                .user(autore)
                .tipo(TipoConsensoEnum.PRIVACY_SANITARIA)
                .concesso(concesso)
                .versioneInformativa(versioneInformativa)
                .dataEvento(ora)
                .ipAddress(ip)
                .note(note == null || note.isBlank() ? null : note.trim())
                .build());
        paziente.setConsensoPrivacy(concesso);
        paziente.setDataConsenso(ora);
        pazienteRepository.save(paziente);
        logger.info("Consenso privacy {} per paziente {} da utente {}", concesso ? "concesso" : "revocato",
                paziente.getId(), autore != null ? autore.getId() : null);
        return c;
    }

    /** Ultimo evento dello storico; senza storico (dati precedenti) vale il flag sul paziente. */
    private boolean attivo(Paziente p) {
        return ultimo(p.getId()).map(Consenso::getConcesso)
                .orElse(Boolean.TRUE.equals(p.getConsensoPrivacy()));
    }

    private Optional<Consenso> ultimo(Integer pazienteId) {
        return consensoRepository.findFirstByPazienteIdAndTipoOrderByDataEventoDescIdDesc(pazienteId, TipoConsensoEnum.PRIVACY_SANITARIA);
    }

    private Paziente paziente(Integer id) {
        return pazienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paziente non trovato"));
    }

    private Users utente(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));
    }

    private ConsensoDTO toDTO(Consenso c) {
        return ConsensoDTO.builder()
                .id(c.getId())
                .tipo(c.getTipo())
                .concesso(c.getConcesso())
                .versioneInformativa(c.getVersioneInformativa())
                .dataEvento(c.getDataEvento())
                .espressoDa(nome(c.getUser()))
                .note(c.getNote())
                .build();
    }

    private static String nome(Users u) {
        return u == null ? null : u.getNome() + " " + u.getCognome();
    }
}
