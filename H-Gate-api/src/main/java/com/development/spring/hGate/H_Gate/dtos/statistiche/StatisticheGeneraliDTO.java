package com.development.spring.hGate.H_Gate.dtos.statistiche;

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
public class StatisticheGeneraliDTO {
    private List<StatGiornoDTO> prenotazioniPerGiorno;
    private List<StatSpecializzazioneDTO> specializzazioniTop;
}
