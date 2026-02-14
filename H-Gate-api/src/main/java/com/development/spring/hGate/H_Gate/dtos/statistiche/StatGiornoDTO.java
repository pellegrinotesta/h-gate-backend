package com.development.spring.hGate.H_Gate.dtos.statistiche;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StatGiornoDTO {
    private String giorno;
    private Integer valore;
}
