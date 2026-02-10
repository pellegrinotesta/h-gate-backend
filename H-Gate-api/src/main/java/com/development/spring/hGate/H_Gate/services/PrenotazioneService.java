package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.SlotDisponibileDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.SlotDisponibiliDTO;
import com.development.spring.hGate.H_Gate.dtos.statistiche.StatGiornoDTO;
import com.development.spring.hGate.H_Gate.dtos.statistiche.StatSpecializzazioneDTO;
import com.development.spring.hGate.H_Gate.dtos.statistiche.StatisticheGeneraliDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.entity.TutoreLegale;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.*;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrenotazioneService extends BasicService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final MedicoRepository medicoRepository;
    private final PazienteRepository pazienteRepository;
    private final TutoreLegaleRepository tutoreLegaleRepository;
    private final PazienteTutoreRepository pazienteTutoreRepository;
    private final TariffeMediciRepository tariffeMediciRepository;
    private final NotificheService notificheService;

    private static final String PAZIENTE_NOT_FOUND = "Paziente non trovato";
    private static final String MEDICO_NOT_FOUND = "Medico non trovato";
    private static final String TUTORE_NOT_AUTHORIZED = "Non sei autorizzato a prenotare per questo paziente";
    private static final String SLOT_NOT_AVAILABLE = "Orario non disponibile";
    private static final String PRENOTAZIONE_NOT_FOUND = "Prenotazione non trovata";


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Prenotazione creaPrenotazione(Integer tutoreUserId, PrenotazioneCreateDTO dto) {
        TutoreLegale tutore = tutoreLegaleRepository.findByUserId(tutoreUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutore non trovato"));

        Paziente paziente = pazienteRepository.findById(dto.getPazienteId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAZIENTE_NOT_FOUND));

        boolean isAuthorized = pazienteTutoreRepository.existsByPazienteIdAndTutoreId(paziente.getId(), tutore.getId());

        if(!isAuthorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, TUTORE_NOT_AUTHORIZED);
        }

        Medico medico = medicoRepository.findById(dto.getMedicoId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MEDICO_NOT_FOUND));

        if(!medico.getIsDisponibile()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il medico non è ancora verificato");
        }

        LocalDateTime dataOra = (dto.getDataOra());
        LocalDateTime now = LocalDateTime.now();

        if(dataOra.isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Non puoi prenotare una visita nel passato");
        }

        if(dataOra.isBefore(now.plusHours(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La prenotazione deve essere effettuata con almeno 1 ora di anticipo");
        }

        int durataMinuti = medico.getDurataVisitaMinuti() != null ? medico.getDurataVisitaMinuti() : 30;
        LocalDateTime dataOraFine = dataOra.plusMinutes(durataMinuti);

        boolean slotDisponibile = verificaDisponibilitaSlot(medico.getId(), dataOra, dataOraFine);

        if(!slotDisponibile) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SLOT_NOT_AVAILABLE
            );
        }

        boolean hasPrenotazioniOggi = prenotazioneRepository.existsByPazienteIdAndDataOraBetween(paziente.getId(), dataOra.toLocalDate().atStartOfDay(), dataOra.toLocalDate().atTime(23,59,59));

        if(hasPrenotazioniOggi) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hai già una prenotazione per questo paziente in questo giorno");
        }

        BigDecimal costo = tariffeMediciRepository
                .findCosto(dto.getMedicoId(), dto.getTipoVisita(), dto.isPrimaVisita())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tariffa non configurata"
                ));

        Prenotazione prenotazione = Prenotazione.builder()
                .uuid(UUID.randomUUID().toString())
                .numeroPrenotazione(generaNumeroPrenotazione())
                .paziente(paziente)
                .medico(medico)
                .dataOra(dataOra)
                .dataOraFine(dataOraFine)
                .tipoVisita(dto.getTipoVisita())
                .stato(StatoPrenotazioneEnum.IN_ATTESA)
                .costo(costo)
                .notePaziente(dto.getNote())
                .isPrimaVisita(dto.isPrimaVisita())
                .isUrgente(false)
                .confermaInviata(false)
                .createdByUserId(tutore.getUser())
                .build();

        prenotazione = prenotazioneRepository.save(prenotazione);

        try {
            notificheService.notificaNuovaPrenotazioneMedico(prenotazione);
            logger.info("Notifica inviata al medico per prenotazione ID: {}", prenotazione.getId());
        } catch (Exception e) {
            logger.error("Errore invio notifica al medico", e);
        }

        return  prenotazione;
    }

    private String generaNumeroPrenotazione() {
        LocalDate today = LocalDate.now();
        String data = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long progressivo = prenotazioneRepository.countByData(today) + 1;
        String progressivoFormatted = String.format("%03d", progressivo);
        return "NPI" + data + progressivoFormatted;
    }

    @Transactional
    public Prenotazione confermaPrenotazione(Integer medicoUserId, Integer prenotazioneId) {
        logger.info("Medico userId: {} conferma prenotazione ID: {}", medicoUserId, prenotazioneId);

        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        PRENOTAZIONE_NOT_FOUND
                ));

        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);

        if (!prenotazione.getMedico().getId().equals(medico.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Non sei autorizzato a confermare questa prenotazione"
            );
        }

        if (prenotazione.getStato() != StatoPrenotazioneEnum.IN_ATTESA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Questa prenotazione non è in attesa di conferma"
            );
        }

        prenotazione.setStato(StatoPrenotazioneEnum.CONFERMATA);
        prenotazione.setConfermaInviata(true);

        prenotazione = prenotazioneRepository.save(prenotazione);
        logger.info("Prenotazione ID: {} confermata", prenotazioneId);

        // Notifica al paziente
        try {
            notificheService.notificaConfermaPrenotazionePaziente(prenotazione);
        } catch (Exception e) {
            logger.error("Errore invio notifica conferma al paziente", e);
        }

        return prenotazione;
    }


    @Transactional
    public String annullaPrenotazione(Integer tutoreUserId, Integer prenotazioneId, String motivo) {

        // Verifica che la prenotazione esista
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prenotazione non trovata"
                ));

        // Verifica che il tutore sia autorizzato
        TutoreLegale tutore = tutoreLegaleRepository.findByUserId(tutoreUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Tutore non trovato"
                ));

        boolean isAuthorized = pazienteTutoreRepository
                .existsByPazienteIdAndTutoreId(
                        prenotazione.getPaziente().getId(),
                        tutore.getId()
                );

        if (!isAuthorized) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Non sei autorizzato ad annullare questa prenotazione"
            );
        }

        // Verifica che la prenotazione non sia già completata o annullata
        if (prenotazione.getStato() == StatoPrenotazioneEnum.COMPLETATA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Non puoi annullare una prenotazione già completata"
            );
        }

        if (prenotazione.getStato() == StatoPrenotazioneEnum.ANNULLATA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Prenotazione già annullata"
            );
        }

        // Verifica che non sia troppo tardi per annullare (es. meno di 24 ore)
        LocalDateTime now = LocalDateTime.now();
        if (prenotazione.getDataOra().isBefore(now.plusHours(24))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Non puoi annullare una prenotazione a meno di 24 ore dall'appuntamento"
            );
        }

        // Annulla la prenotazione
        prenotazione.setStato(StatoPrenotazioneEnum.ANNULLATA);
        prenotazione.setMotivoAnnullamento(motivo);
        prenotazione.setDataAnnullamento(now);
        prenotazione.setAnnullataDa(tutore.getUser());

        prenotazioneRepository.save(prenotazione);

        return "Prenotazione annullata con successo";
    }


    private boolean verificaDisponibilitaSlot(Integer medicoId, LocalDateTime dataOra, LocalDateTime dataOraFine) {
        List<Prenotazione> conflitti = prenotazioneRepository.findConflittingAppointments(medicoId, dataOra, dataOraFine, List.of(StatoPrenotazioneEnum.CONFERMATA.name(), StatoPrenotazioneEnum.IN_ATTESA.name()));

        return conflitti.isEmpty();
    }

    public StatisticheGeneraliDTO getStatisticheGenerali() {
        // Prenotazioni per giorno della settimana (ultimi 7 giorni)
        List<StatGiornoDTO> prenotazioniPerGiorno = List.of(
                StatGiornoDTO.builder().giorno("Lunedì").valore(
                        prenotazioneRepository.countByDayOfWeek(1)
                ).build(),
                StatGiornoDTO.builder().giorno("Martedì").valore(
                        prenotazioneRepository.countByDayOfWeek(2)
                ).build(),
                StatGiornoDTO.builder().giorno("Mercoledì").valore(
                        prenotazioneRepository.countByDayOfWeek(3)
                ).build(),
                StatGiornoDTO.builder().giorno("Giovedì").valore(
                        prenotazioneRepository.countByDayOfWeek(4)
                ).build(),
                StatGiornoDTO.builder().giorno("Venerdì").valore(
                        prenotazioneRepository.countByDayOfWeek(5)
                ).build()
        );

        // Top 5 specializzazioni più richieste
        List<StatSpecializzazioneDTO> specializzazioniTop = medicoRepository
                .findTopSpecializzazioni()
                .stream()
                .limit(5)
                .map(row -> StatSpecializzazioneDTO.builder()
                        .specializzazione((String) row[0])
                        .valore(((Number) row[1]).intValue())
                        .build()
                )
                .collect(Collectors.toList());

        return StatisticheGeneraliDTO.builder()
                .prenotazioniPerGiorno(prenotazioniPerGiorno)
                .specializzazioniTop(specializzazioniTop)
                .build();
    }

    public Integer prenotazioniOggi() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return prenotazioneRepository.countByDataOraBetween(startOfDay, endOfDay);
    }

    public BigDecimal fatturatoMensile() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(
                LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
        return prenotazioneRepository.sumCostoByStatoAndDataOraBetween(StatoPrenotazioneEnum.COMPLETATA, startOfMonth, endOfMonth);
    }

    @Transactional(readOnly = true)
    public SlotDisponibiliDTO getSlotDisponibili(Integer medicoId, String data) {

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Medico non trovato"));

        LocalDate date = LocalDate.parse(data);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Prenotazioni già esistenti per quel giorno
        List<Prenotazione> prenotazioni = prenotazioneRepository
                .findByMedicoIdAndDataOraBetween(medicoId, startOfDay, endOfDay);

        // ⬇️ esempio: orario lavorativo fisso (adatta se usi JSON o tabella)
        LocalTime inizioLavoro = LocalTime.of(9, 0);
        LocalTime fineLavoro = LocalTime.of(17, 0);

        int durata = medico.getDurataVisitaMinuti();
        int pausa = medico.getPausaTraVisiteMinuti();

        List<SlotDisponibileDTO> slots = new ArrayList<>();

        LocalDateTime current = date.atTime(inizioLavoro);
        LocalDateTime fine = date.atTime(fineLavoro);

        while (!current.plusMinutes(durata).isAfter(fine)) {

            LocalDateTime slotEnd = current.plusMinutes(durata);

            LocalDateTime finalCurrent = current;
            boolean occupato = prenotazioni.stream().anyMatch(p ->
                    overlaps(finalCurrent, slotEnd, p.getDataOra(),
                            p.getDataOra().plusMinutes(durata))
            );

            slots.add(
                    SlotDisponibileDTO.builder()
                            .dataOra(current)
                            .dataOraFine(slotEnd)
                            .disponibile(!occupato)
                            .motivoNonDisponibilita(
                                    occupato ? "Slot già prenotato" : null
                            )
                            .build()
            );

            current = slotEnd.plusMinutes(pausa);
        }

        return SlotDisponibiliDTO.builder()
                .medicoId(medico.getId())
                .medicoNome("Dr. " + medico.getUser().getNome() + " " + medico.getUser().getCognome())
                .data(data)
                .durataVisitaMinuti(durata)
                .slots(slots)
                .build();
    }

    private boolean overlaps(
            LocalDateTime start1,
            LocalDateTime end1,
            LocalDateTime start2,
            LocalDateTime end2
    ) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }


}
