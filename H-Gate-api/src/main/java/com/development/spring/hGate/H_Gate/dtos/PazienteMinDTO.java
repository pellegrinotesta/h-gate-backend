package com.development.spring.hGate.H_Gate.dtos;

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
public class PazienteMinDTO {

    private Integer id;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
}
