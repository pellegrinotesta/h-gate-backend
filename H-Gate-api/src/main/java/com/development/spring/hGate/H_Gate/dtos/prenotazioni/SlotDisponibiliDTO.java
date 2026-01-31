package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDisponibiliDTO {

    private Long medicoId;
    private String medicoNome;
    private String data;
    private List<SlotDisponibileDTO> slots;
    private Integer durataVisitaMinuti;
}
