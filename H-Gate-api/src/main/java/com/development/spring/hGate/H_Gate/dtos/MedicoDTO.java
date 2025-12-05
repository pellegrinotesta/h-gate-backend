package com.development.spring.hGate.H_Gate.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MedicoDTO {

    private String specializzazione;
    private String numeroAlbo;
    private String universita;
    private Integer annoLaurea;
    private String bio;
    private String curriculum;
    private String tariffe;
    private String orariDisponibilita;
    private Integer durataVisitaMinuti = 30;
    private Integer pausaTraVisiteMinuti = 5;
    private Integer anticipoPrenotazioneGiorni = 30;
    private Boolean isDisponibile = true;
    private Boolean isVerificato = false;
    private LocalDateTime dataVerifica;
    private UserDTO verificatoDa;
    private BigDecimal ratingMedio = BigDecimal.ZERO;
    private Integer numeroRecensioni = 0;
    private Integer numeroPazienti = 0;


}
