package com.development.spring.hGate.H_Gate.dtos.pazienti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValutazioneUpdateDTO {
    private String tipoTest;
    private String punteggi;
    private String interpretazione;
}
