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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                    emailService.inviaEmail(user.getEmail(), titolo, messaggio);
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
    public void notificaNuovaPrenotazioneMedico(Prenotazione prenotazione) {
        Users medicoUser = prenotazione.getMedico().getUser();

        String titolo = "Nuova prenotazione da confermare";

        String messaggio = String.format(
                "Hai ricevuto una nuova prenotazione per il %s.\n\n" +
                        "Paziente: %s %s\n" +
                        "Tipo visita: %s\n" +
                        "Numero: %s\n\n" +
                        "Accedi per confermare o rifiutare l'appuntamento.",
                prenotazione.getDataOra().format(DATE_FORMATTER),
                prenotazione.getPaziente().getNome(),
                prenotazione.getPaziente().getCognome(),
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
                true // Invia anche email
        );
    }

    /**
     * Notifica al paziente: prenotazione confermata dal medico
     */
    public void notificaConfermaPrenotazionePaziente(Prenotazione prenotazione) {

        Users pazienteUser = getTutoreUser(prenotazione);

        String titolo = "Prenotazione confermata";

        String messaggio = String.format(
                "La tua prenotazione è stata confermata!\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n" +
                        "Tipo visita: %s\n" +
                        "Costo: €%.2f\n\n" +
                        "Ti ricordiamo di presentarti 10 minuti prima.",
                prenotazione.getMedico().getUser().getNome(),
                prenotazione.getMedico().getUser().getCognome(),
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
                true // Invia anche email
        );
    }

    /**
     * Promemoria 24 ore prima dell'appuntamento
     */
    public void inviaPromemoria24Ore(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

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
                prenotazione.getMedico().getUser().getNome(),
                prenotazione.getMedico().getUser().getCognome(),
                prenotazione.getDataOra().format(DATE_FORMATTER)
        );

        String link = "/tutore/prenotazioni/" + prenotazione.getId();

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.PROMEMORIA,
                titolo,
                messaggio,
                link,
                true // Invia anche email
        );
    }

    /**
     * Notifica al medico: prenotazione annullata dal paziente
     */
    public void notificaAnnullamentoMedico(Prenotazione prenotazione) {
        Users medicoUser = prenotazione.getMedico().getUser();

        String titolo = "Prenotazione annullata";

        String messaggio = String.format(
                "Una prenotazione è stata annullata.\n\n" +
                        "Paziente: %s %s\n" +
                        "Data e ora: %s\n" +
                        "Numero: %s\n" +
                        "Motivo: %s\n\n" +
                        "Lo slot è ora nuovamente disponibile.",
                prenotazione.getPaziente().getNome(),
                prenotazione.getPaziente().getCognome(),
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
                true // Invia anche email
        );
    }

    /**
     * Conferma annullamento al paziente
     */
    public void notificaAnnullamentoPaziente(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        String titolo = "Prenotazione annullata";

        String messaggio = String.format(
                "La tua prenotazione è stata annullata.\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n\n" +
                        "Puoi effettuare una nuova prenotazione in qualsiasi momento.",
                prenotazione.getMedico().getUser().getNome(),
                prenotazione.getMedico().getUser().getCognome(),
                prenotazione.getDataOra().format(DATE_FORMATTER)
        );

        String link = "/tutore/medici";

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.ANNULLAMENTO_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true // Invia anche email
        );
    }

    /**
     * Notifica al paziente: prenotazione rifiutata dal medico
     */
    public void notificaRifiutoPrenotazionePaziente(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        String titolo = "Prenotazione non confermata";

        String messaggio = String.format(
                "La tua prenotazione non è stata confermata dal medico.\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n" +
                        "Motivo: %s\n\n" +
                        "Puoi cercare un altro medico o scegliere un'altra data.",
                prenotazione.getMedico().getUser().getNome(),
                prenotazione.getMedico().getUser().getCognome(),
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
                true // Invia anche email
        );
    }

    /**
     * Notifica cancellazione automatica (prenotazione scaduta)
     */
    public void notificaCancellazioneAutomatica(Prenotazione prenotazione) {
        Users pazienteUser = getTutoreUser(prenotazione);

        String titolo = "Prenotazione scaduta";

        String messaggio = String.format(
                "La tua prenotazione è stata annullata automaticamente.\n\n" +
                        "Medico: Dr. %s %s\n" +
                        "Data e ora: %s\n\n" +
                        "Il medico non ha confermato la prenotazione entro le 48 ore.\n" +
                        "Puoi effettuare una nuova prenotazione.",
                prenotazione.getMedico().getUser().getNome(),
                prenotazione.getMedico().getUser().getCognome(),
                prenotazione.getDataOra().format(DATE_FORMATTER)
        );

        String link = "/tutore/medici";

        creaNotifica(
                pazienteUser,
                TipoNotificaEnum.ANNULLAMENTO_PRENOTAZIONE,
                titolo,
                messaggio,
                link,
                true // Invia anche email
        );
    }

    // ========== METODI UTILITY ==========

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
    public Long contaNonLette(Integer userId) {
        return notificaRepository.countByUserIdAndIsLettaFalse(userId);
    }

    private Users getTutoreUser(Prenotazione prenotazione) {
        return prenotazione.getPaziente()
                .getTutori()
                .stream()
                .findFirst() // regola: primo tutore
                .map(pt -> pt.getTutore().getUser())
                .orElseThrow(() ->
                        new IllegalStateException("Nessun tutore associato al paziente"));
    }

}
