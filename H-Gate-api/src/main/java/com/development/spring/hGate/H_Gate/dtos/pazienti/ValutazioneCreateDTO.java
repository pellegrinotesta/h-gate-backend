package com.development.spring.hGate.H_Gate.dtos.pazienti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValutazioneCreateDTO {
    private Integer pazienteId;
    private String tipoTest;
    private String punteggi; // JSON string
    private String interpretazione;
}
