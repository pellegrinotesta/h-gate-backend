package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.StatGiornoDTO;
import com.development.spring.hGate.H_Gate.dtos.StatSpecializzazioneDTO;
import com.development.spring.hGate.H_Gate.dtos.StatisticheGeneraliDTO;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrenotazioneService extends BasicService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final MedicoRepository medicoRepository;

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

}
