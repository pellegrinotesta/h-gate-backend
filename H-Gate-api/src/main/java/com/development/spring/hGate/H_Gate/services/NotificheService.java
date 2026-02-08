package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Notifica;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum;
import com.development.spring.hGate.H_Gate.repositories.NotificheRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificheService extends BasicService {

    private final NotificheRepository notificaRepository;
    private final EmailService emailService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Logger log = LoggerFactory.getLogger(NotificheService.class);

    /**
     * Crea notifica in-app (e opzionalmente invia email)
     * Usa REQUIRES_NEW per non bloccare la transazione principale se fallisce
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void creaNotifica(
            Users user,
            TipoNotificaEnum tipo,
            String titolo,
            String messaggio,
            String link,
            boolean inviaEmail
    ) {
        try {
            // 1. Crea notifica in-app
            Notifica notifica = Notifica.builder()
                    .user(user)
                    .tipo(tipo)
                    .titolo(titolo)
                    .messaggio(messaggio)
                    .link(link)
                    .isLetta(false)
                    .isInviataEmail(false)
                    .build();

            notifica = notificaRepository.save(notifica);

            // 2. Invia email se richiesto
            if (inviaEmail) {
                try {
                    // Accedi all'email DENTRO la transazione
                    String userEmail = user.getEmail();
                    emailService.inviaEmail(userEmail, titolo, messaggio);
                    notifica.setIsInviataEmail(true);
                    notifica.setDataInvioEmail(LocalDateTime.now());
                    notificaRepository.save(notifica);
                } catch (Exception e) {
                    log.error("Errore invio email per notifica ID: {}", notifica.getId(), e);
                    // Non fallire se l'email non parte, la notifica in-app è già salvata
                }
            }

            log.info("Notifica creata - User: {}, Tipo: {}", user.getId(), tipo);

        } catch (Exception e) {
            log.error("Errore creazione notifica per user ID: {}", user.getId(), e);
            throw e;
        }
    }

    // ========== NOTIFICHE SPECIFICHE PER PRENOTAZIONI ==========

    /**
     * Notifica al medico: nuova prenotazione da confermare
     */
    @Transactional(readOnly = true)
    public void notificaNuovaPrenotazioneMedico(Prenotazione prenotazione) {
        Users medicoUser = prenotazione.getMedico().getUser();

        // Inizializza i dati lazy DENTRO la transazione
        String medicoNome = medicoUser.getNome();
        String pazienteNome = prenotazione.getPaziente().getNome();
        String pazienteCognome = prenotazione.getPaziente().getCognome();

        String titolo = "Nuova prenotazione da confermare";

        String messaggio = String.format(
                "Hai ricevuto una nuova prenotazione per il %s.\n\n" +
                        "Paziente: %s %s\n" +
                        "Tipo visita: %s\n" +
                        "Numero: %s\n\n" +
                        "Accedi per confermare o rifiutare l'appuntamento.",
                prenotazione.getDataOra().format(DATE_FORMATTER),
                pazienteNome,
                pazienteCognome,
                prenotazione.getTipoVisita(),
                prenotazione.getNumeroPrenotazione()
        );

        String link = "/medico/prenotazioni/" + prenotazione.getId();

        creaNotifica(
                medicoUser,
                TipoNotificaEnum.NUOVA_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true
        );
    }

    /**
     * Notifica al paziente: prenotazione confermata dal medico
     */
    @Transactional(readOnly = true)
    public void notificaConfermaPrenotazionePaziente(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        // Inizializza lazy loads
        String medicoNome = prenotazione.getMedico().getUser().getNome();
        String medicoCognome = prenotazione.getMedico().getUser().getCognome();

        String titolo = "Prenotazione confermata";

        String messaggio = String.format(
                "La tua prenotazione è stata confermata!\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n" +
                        "Tipo visita: %s\n" +
                        "Costo: €%.2f\n\n" +
                        "Ti ricordiamo di presentarti 10 minuti prima.",
                medicoNome,
                medicoCognome,
                prenotazione.getDataOra().format(DATE_FORMATTER),
                prenotazione.getTipoVisita(),
                prenotazione.getCosto()
        );

        String link = "/tutore/prenotazioni/" + prenotazione.getId();

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.CONFERMA_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true
        );
    }

    /**
     * Promemoria 24 ore prima dell'appuntamento
     */
    @Transactional(readOnly = true)
    public void inviaPromemoria24Ore(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        // Inizializza lazy loads
        String medicoNome = prenotazione.getMedico().getUser().getNome();
        String medicoCognome = prenotazione.getMedico().getUser().getCognome();

        String titolo = "Promemoria: appuntamento domani";

        String messaggio = String.format(
                "Ti ricordiamo che domani hai un appuntamento:\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n" +
                        "Luogo: [INSERIRE INDIRIZZO]\n\n" +
                        "Ricorda di:\n" +
                        "✓ Presentarti 10 minuti prima\n" +
                        "✓ Portare documento di identità\n" +
                        "✓ Portare eventuali esami precedenti",
                medicoNome,
                medicoCognome,
                prenotazione.getDataOra().format(DATE_FORMATTER)
        );

        String link = "/tutore/prenotazioni/" + prenotazione.getId();

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.PROMEMORIA,
                titolo,
                messaggio,
                link,
                true
        );
    }

    /**
     * Notifica al medico: prenotazione annullata dal paziente
     */
    @Transactional(readOnly = true)
    public void notificaAnnullamentoMedico(Prenotazione prenotazione) {
        Users medicoUser = prenotazione.getMedico().getUser();

        // Inizializza lazy loads
        String pazienteNome = prenotazione.getPaziente().getNome();
        String pazienteCognome = prenotazione.getPaziente().getCognome();

        String titolo = "Prenotazione annullata";

        String messaggio = String.format(
                "Una prenotazione è stata annullata.\n\n" +
                        "Paziente: %s %s\n" +
                        "Data e ora: %s\n" +
                        "Numero: %s\n" +
                        "Motivo: %s\n\n" +
                        "Lo slot è ora nuovamente disponibile.",
                pazienteNome,
                pazienteCognome,
                prenotazione.getDataOra().format(DATE_FORMATTER),
                prenotazione.getNumeroPrenotazione(),
                prenotazione.getMotivoAnnullamento() != null
                        ? prenotazione.getMotivoAnnullamento()
                        : "Non specificato"
        );

        String link = "/medico/prenotazioni";

        creaNotifica(
                medicoUser,
                TipoNotificaEnum.ANNULLAMENTO_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true
        );
    }

    /**
     * Conferma annullamento al paziente
     */
    @Transactional(readOnly = true)
    public void notificaAnnullamentoPaziente(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        // Inizializza lazy loads
        String medicoNome = prenotazione.getMedico().getUser().getNome();
        String medicoCognome = prenotazione.getMedico().getUser().getCognome();

        String titolo = "Prenotazione annullata";

        String messaggio = String.format(
                "La tua prenotazione è stata annullata.\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n\n" +
                        "Puoi effettuare una nuova prenotazione in qualsiasi momento.",
                medicoNome,
                medicoCognome,
                prenotazione.getDataOra().format(DATE_FORMATTER)
        );

        String link = "/tutore/medici";

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.ANNULLAMENTO_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true
        );
    }

    /**
     * Notifica al paziente: prenotazione rifiutata dal medico
     */
    @Transactional(readOnly = true)
    public void notificaRifiutoPrenotazionePaziente(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        // Inizializza lazy loads
        String medicoNome = prenotazione.getMedico().getUser().getNome();
        String medicoCognome = prenotazione.getMedico().getUser().getCognome();

        String titolo = "Prenotazione non confermata";

        String messaggio = String.format(
                "La tua prenotazione non è stata confermata dal medico.\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n" +
                        "Motivo: %s\n\n" +
                        "Puoi cercare un altro medico o scegliere un'altra data.",
                medicoNome,
                medicoCognome,
                prenotazione.getDataOra().format(DATE_FORMATTER),
                prenotazione.getMotivoAnnullamento() != null
                        ? prenotazione.getMotivoAnnullamento()
                        : "Non specificato"
        );

        String link = "/tutore/medici";

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.RIFIUTO_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true
        );
    }

    /**
     * Notifica cancellazione automatica (prenotazione scaduta)
     */
    @Transactional(readOnly = true)
    public void notificaCancellazioneAutomatica(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        // Inizializza lazy loads
        String medicoNome = prenotazione.getMedico().getUser().getNome();
        String medicoCognome = prenotazione.getMedico().getUser().getCognome();

        String titolo = "Prenotazione scaduta";

        String messaggio = String.format(
                "La tua prenotazione è stata annullata automaticamente.\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n\n" +
                        "Il medico non ha confermato la prenotazione entro le 48 ore.\n" +
                        "Puoi effettuare una nuova prenotazione.",
                medicoNome,
                medicoCognome,
                prenotazione.getDataOra().format(DATE_FORMATTER)
        );

        String link = "/tutore/medici";

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.ANNULLAMENTO_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true
        );
    }

    // ========== METODI UTILITY ==========

    /**
     * Helper per ottenere il tutore principale del paziente
     * DEVE essere chiamato DENTRO una transazione
     */
    private Users getTutoreUser(Prenotazione prenotazione) {
        return prenotazione.getPaziente()
                .getTutori()
                .stream()
                .findFirst()
                .map(pt -> pt.getTutore().getUser())
                .orElseThrow(() ->
                        new IllegalStateException("Nessun tutore associato al paziente"));
    }

    /**
     * Marca notifica come letta
     */
    @Transactional
    public void marcaComeLetta(Integer notificaId, Integer userId) {
        Notifica notifica = notificaRepository.findById(notificaId)
                .orElseThrow(() -> new IllegalArgumentException("Notifica non trovata"));

        if (!notifica.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Non autorizzato");
        }

        if (!notifica.getIsLetta()) {
            notifica.setIsLetta(true);
            notifica.setDataLettura(LocalDateTime.now());
            notificaRepository.save(notifica);
        }
    }

    /**
     * Marca tutte le notifiche come lette per un utente
     */
    @Transactional
    public void marcaTutteComeLette(Integer userId) {
        notificaRepository.marcaTutteComeLette(userId, LocalDateTime.now());
    }

    /**
     * Conta notifiche non lette
     */
    @Transactional(readOnly = true)
    public Long contaNonLette(Integer userId) {
        return notificaRepository.countByUserIdAndIsLettaFalse(userId);
    }

    /**
     * Ottiene tutte le notifiche di un utente (paginate)
     */
    @Transactional(readOnly = true)
    public Page<Notifica> getNotificheUtente(Integer userId, Pageable pageable) {
        return notificaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Ottiene le notifiche non lette
     */
    @Transactional(readOnly = true)
    public List<Notifica> getNotificheNonLette(Integer userId) {
        return notificaRepository.findByUserIdAndIsLettaFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Elimina una notifica (con controllo autorizzazione)
     */
    @Transactional
    public void eliminaNotifica(Integer notificaId, Integer userId) {
        Notifica notifica = notificaRepository.findById(notificaId)
                .orElseThrow(() -> new IllegalArgumentException("Notifica non trovata"));

        if (!notifica.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Non autorizzato");
        }

        notificaRepository.delete(notifica);
    }
}