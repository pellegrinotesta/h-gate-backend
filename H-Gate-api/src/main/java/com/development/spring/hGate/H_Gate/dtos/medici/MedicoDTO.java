package com.development.spring.hGate.H_Gate.dtos.medici;

import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String orariDisponibilita;
    private Integer durataVisitaMinuti;
    private Integer pausaTraVisiteMinuti;
    private Integer anticipoPrenotazioneGiorni;
    private Boolean isDisponibile = true;
    private LocalDateTime dataVerifica;
    private UserDTO verificatoDa;
    private BigDecimal ratingMedio;
    private Integer numeroRecensioni;
    private Integer numeroPazienti;


}
