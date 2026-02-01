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

    private Integer id;
    private UserDTO user;
    private String specializzazione;
    private String numeroAlbo;
    private String universita;
    private Integer annoLaurea;
    private String bio;
    private String curriculum;
    private String tariffe;
    private String orariDisponibilita;
    private Integer durataVisitaMinuti;
    private Integer pausaTraVisiteMinuti;
    private Integer anticipoPrenotazioneGiorni;
    private Boolean isDisponibile = true;
    private Boolean isVerificato = false;
    private LocalDateTime dataVerifica;
    private UserDTO verificatoDa;
    private BigDecimal ratingMedio;
    private Integer numeroRecensioni;
    private Integer numeroPazienti;


}
