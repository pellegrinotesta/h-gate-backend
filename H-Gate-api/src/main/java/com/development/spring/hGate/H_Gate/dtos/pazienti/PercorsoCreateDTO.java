package com.development.spring.hGate.H_Gate.dtos.pazienti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PercorsoCreateDTO {

    private Integer pazienteId;
    private String titolo;
    private String obiettivi;
    private LocalDateTime dataFinePrevista;
    private Integer numeroSedutePreviste;
}
