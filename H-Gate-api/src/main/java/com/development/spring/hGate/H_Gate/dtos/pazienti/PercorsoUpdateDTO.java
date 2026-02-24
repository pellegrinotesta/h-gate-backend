package com.development.spring.hGate.H_Gate.dtos.pazienti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PercorsoUpdateDTO {

    private String titolo;
    private String obiettivi;
    private LocalDateTime dataFinePrevista;
    private String stato;
    private Integer numeroSedutePreviste;
    private Integer numeroSeduteEffettuate;
}
