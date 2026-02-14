package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDisponibiliDTO {

    private Integer medicoId;
    private String medicoNome;
    private String data;
    private LocalTime orarioInizio;
    private LocalTime orarioFine;
    private List<SlotDisponibileDTO> slots;
    private Integer durataVisitaMinuti;
    private String message;
}
