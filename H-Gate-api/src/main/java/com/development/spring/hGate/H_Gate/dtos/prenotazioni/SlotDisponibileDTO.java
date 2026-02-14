package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDisponibileDTO {

    private LocalDateTime dataOra;
    private LocalDateTime dataOraFine;
    private Boolean disponibile;
    private String motivoNonDisponibilita;
}
