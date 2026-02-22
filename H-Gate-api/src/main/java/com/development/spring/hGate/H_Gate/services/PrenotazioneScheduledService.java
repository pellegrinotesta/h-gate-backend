package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrenotazioneScheduledService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final NotificheService notificaService;

    /**
     * PROMEMORIA 24 ORE PRIMA
     * Eseguito ogni ora
     */
    @Scheduled(cron = "0 0 * * * *") // Ogni ora in punto
    @Transactional
    public void inviaPromemoria24Ore() {
        log.info("=== INIZIO INVIO PROMEMORIA 24H ===");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime domaniInizio = now.plusHours(23);
        LocalDateTime domaniFine = now.plusHours(25);

        // Trova prenotazioni confermate tra 23 e 25 ore
        // che non hanno ancora ricevuto il promemoria
        List<Prenotazione> prenotazioni = prenotazioneRepository
                .findByStatoAndDataOraBetweenAndPromemoriaInviatoFalse(
                        StatoPrenotazioneEnum.CONFERMATA,
                        domaniInizio,
                        domaniFine
                );

        log.info("Trovate {} prenotazioni per promemoria", prenotazioni.size());

        int inviati = 0;
        for (Prenotazione prenotazione : prenotazioni) {
            try {
                notificaService.inviaPromemoria24Ore(prenotazione);

                // Marca come inviato
                prenotazione.setPromemoriaInviato(true);
                prenotazioneRepository.save(prenotazione);

                inviati++;
            } catch (Exception e) {
                log.error("Errore invio promemoria per prenotazione ID: {}",
                        prenotazione.getId(), e);
            }
        }

        log.info("=== COMPLETATO INVIO PROMEMORIA: {}/{} ===", inviati, prenotazioni.size());
    }

    /**
     * CANCELLAZIONE AUTOMATICA PRENOTAZIONI SCADUTE
     * Prenotazioni IN_ATTESA non confermate dopo 48 ore
     * Eseguito ogni giorno alle 2:00
     */
    @Scheduled(cron = "0 0 2 * * *") // Ogni giorno alle 2:00
    @Transactional
    public void cancellaPrenotazioniScadute() {
        log.info("=== INIZIO CANCELLAZIONE PRENOTAZIONI SCADUTE ===");

        LocalDateTime limite = LocalDateTime.now().minusHours(48);

        List<Prenotazione> prenotazioniScadute = prenotazioneRepository
                .findByStatoAndCreatedAtBefore(
                        StatoPrenotazioneEnum.IN_ATTESA,
                        limite
                );

        log.info("Trovate {} prenotazioni scadute da cancellare", prenotazioniScadute.size());

        int cancellate = 0;
        for (Prenotazione prenotazione : prenotazioniScadute) {
            try {
                prenotazione.setStato(StatoPrenotazioneEnum.ANNULLATA);
                prenotazione.setMotivoAnnullamento(
                        "Cancellazione automatica: non confermata dal medico entro 48 ore"
                );
                prenotazione.setDataAnnullamento(LocalDateTime.now());

                prenotazioneRepository.save(prenotazione);

                // Notifica al paziente
                notificaService.notificaCancellazioneAutomatica(prenotazione);

                cancellate++;
            } catch (Exception e) {
                log.error("Errore cancellazione automatica prenotazione ID: {}",
                        prenotazione.getId(), e);
            }
        }

        log.info("=== COMPLETATA CANCELLAZIONE: {}/{} ===", cancellate, prenotazioniScadute.size());
    }

    /**
     * CANCELLAZIONE PRENOTAZIONI NON CONFERMATE
     * Se IN_ATTESA e manca meno di 24h → ANNULLA
     * Eseguito ogni ora
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cancellaPrenotazioniNonConfermate() {
        log.info("=== INIZIO CANCELLAZIONE NON CONFERMATE ===");

        LocalDateTime tra24Ore = LocalDateTime.now().plusHours(24);

        // Prenotazioni IN_ATTESA che iniziano tra meno di 24h
        List<Prenotazione> prenotazioniDaCancellare = prenotazioneRepository
                .findByStatoAndDataOraBefore(
                        StatoPrenotazioneEnum.IN_ATTESA,
                        tra24Ore
                );

        log.info("Trovate {} prenotazioni non confermate da cancellare",
                prenotazioniDaCancellare.size());

        int cancellate = 0;
        for (Prenotazione p : prenotazioniDaCancellare) {
            try {
                p.setStato(StatoPrenotazioneEnum.ANNULLATA);
                p.setMotivoAnnullamento(
                        "Cancellazione automatica: medico non ha confermato entro 24h dalla visita"
                );
                p.setDataAnnullamento(LocalDateTime.now());
                prenotazioneRepository.save(p);

                // INVALIDA notifiche vecchie
                //notificaService.invalidaNotifichePrenotazione(p.getId());

                // NUOVA notifica di cancellazione
                notificaService.notificaCancellazioneAutomaticaTutore(p);

                cancellate++;
            } catch (Exception e) {
                log.error("Errore cancellazione prenotazione {}", p.getId(), e);
            }
        }

        log.info("=== CANCELLATE {}/{} ===", cancellate, prenotazioniDaCancellare.size());
    }

    @Transactional
    public void gestisciNoShow() {
        log.info("=== INIZIO GESTIONE NO-SHOW ===");

        LocalDateTime unaOraFa = LocalDateTime.now().minusHours(1);

        // Prenotazioni confermate terminate da più di 1 ora
        List<Prenotazione> prenotazioniPassate = prenotazioneRepository
                .findByStatoAndDataOraFineBefore(
                        StatoPrenotazioneEnum.CONFERMATA,
                        unaOraFa
                );

        log.info("Trovate {} prenotazioni potenzialmente NO_SHOW", prenotazioniPassate.size());

        int marcate = 0;
        for (Prenotazione prenotazione : prenotazioniPassate) {
            try {
                prenotazione.setStato(StatoPrenotazioneEnum.NON_PRESENTATO);
                prenotazione.setMotivoAnnullamento("Paziente non presentato - marcato automaticamente");

                prenotazioneRepository.save(prenotazione);

                log.info("Prenotazione ID {} marcata come NO_SHOW", prenotazione.getId());
                marcate++;

            } catch (Exception e) {
                log.error("Errore gestione NO_SHOW per prenotazione ID: {}",
                        prenotazione.getId(), e);
            }
        }

        log.info("=== COMPLETATA GESTIONE NO-SHOW: {}/{} ===", marcate, prenotazioniPassate.size());
    }

    /**
     * PROMEMORIA AL MEDICO
     * Se IN_ATTESA da più di 24h → sollecita conferma
     */
    @Scheduled(cron = "0 0 10 * * *") // Ogni giorno alle 10:00
    @Transactional
    public void sollecitaConfermaMedico() {
        log.info("=== SOLLECITO CONFERMA MEDICI ===");

        LocalDateTime limite = LocalDateTime.now().minusHours(24);

        List<Prenotazione> prenotazioniDaSollecitare = prenotazioneRepository
                .findByStatoAndCreatedAtBeforeAndConfermaInviataFalse(
                        StatoPrenotazioneEnum.IN_ATTESA,
                        limite
                );

        log.info("Trovate {} prenotazioni da sollecitare",
                prenotazioniDaSollecitare.size());

        int sollecitate = 0;
        for (Prenotazione p : prenotazioniDaSollecitare) {
            try {
                notificaService.sollecitaConfermaMedico(p);

                p.setConfermaInviata(true);
                prenotazioneRepository.save(p);

                sollecitate++;
            } catch (Exception e) {
                log.error("Errore sollecito {}", p.getId(), e);
            }
        }

        log.info("=== SOLLECITATE {}/{} ===", sollecitate, prenotazioniDaSollecitare.size());
    }


}
