package com.development.spring.hGate.H_Gate.dtos.statistiche;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendMensileDTO {

    private String mese;
    private Long valore;
    private BigDecimal valoreDecimale;
    private Double percentualeVariazione;
    private String labelBreve;
}
