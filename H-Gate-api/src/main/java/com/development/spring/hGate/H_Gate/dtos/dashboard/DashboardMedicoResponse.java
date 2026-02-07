package com.development.spring.hGate.H_Gate.dtos.dashboard;

import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
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
public class DashboardMedicoResponse {

    private String nomeMedico;
    private Integer visiteOggi;
    private Integer pazientiTotali;
    private Integer refertiDaFirmare;
    private Integer refertiDaCompletare;
    private BigDecimal ratingMedio;
    private Integer numeroRecensioni;
    private List<PrenotazioneDTO> appuntamentiOggi;
}
