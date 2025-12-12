package com.development.spring.hGate.H_Gate.dtos;

import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DashboardResponse {

    private List<VPrenotazioniDettagliate> prenotazioni;
    private List<RefertoDTO> referti;
}
