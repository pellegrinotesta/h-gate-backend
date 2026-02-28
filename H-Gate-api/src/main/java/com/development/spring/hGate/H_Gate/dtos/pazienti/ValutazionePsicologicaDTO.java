package com.development.spring.hGate.H_Gate.dtos.pazienti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ValutazionePsicologicaDTO {

    private Integer id;
    private Integer pazienteId;
    private Integer medicoId;
    private LocalDateTime dataValutazione;
    private String tipoTest;
    private String punteggi;
    private String interpretazione;

    private String medicoNome;
    private String medicoCognome;
}
