package com.development.spring.hGate.H_Gate.dtos.pazienti;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
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
public class PercorsiTerapeuticiDTO {

    private Integer id;
    private String titolo;
    private String obiettivi;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFinePrevista;
    private String stato;
    private Integer numeroSedutePreviste;
    private Integer numeroSeduteEffettuate;
    private LocalDateTime createdAt;
    private String medicoNome;
    private String medicoCognome;
    private Integer progressoPercentuale;
}
