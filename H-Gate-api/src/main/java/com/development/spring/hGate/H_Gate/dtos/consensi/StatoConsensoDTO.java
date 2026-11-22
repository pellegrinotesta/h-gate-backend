package com.development.spring.hGate.H_Gate.dtos.consensi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Stato attuale del consenso privacy di un minore. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatoConsensoDTO {
    private Integer pazienteId;
    private String pazienteNome;
    private String pazienteCognome;
    /** true se l'ultimo evento è una concessione */
    private boolean attivo;
    /** true se il consenso è attivo e riferito all'informativa in vigore */
    private boolean aggiornato;
    private String versioneConsenso;
    private String versioneCorrente;
    private LocalDateTime dataEvento;
    private String espressoDa;
    private List<ConsensoDTO> storico;
}
