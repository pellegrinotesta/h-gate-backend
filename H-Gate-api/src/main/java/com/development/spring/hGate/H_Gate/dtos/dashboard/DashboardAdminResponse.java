package com.development.spring.hGate.H_Gate.dtos.dashboard;

import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDaVerificareDTO;
import com.development.spring.hGate.H_Gate.dtos.statistiche.StatisticheGeneraliDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DashboardAdminResponse {
    private Integer pazientiAttivi;
    private Integer mediciAttivi;
    private Integer prenotazioniOggi;
    private BigDecimal fatturatoMensile;
    private Integer mediciDaVerificare;
    private List<MedicoDaVerificareDTO> mediciInAttesa;
    private StatisticheGeneraliDTO statistiche;
}
