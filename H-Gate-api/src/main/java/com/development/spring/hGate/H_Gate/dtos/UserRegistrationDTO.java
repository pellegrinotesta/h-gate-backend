package com.development.spring.hGate.H_Gate.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRegistrationDTO {

    private String confirmPassword;
    private String email;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private Date dataNascita;
    private String telefono;
    private String indirizzo;
    private String citta;
    private String provincia;
    private String cap;
    private String ruolo;
    private String allergie;
    private String altezzaCm;
    private String gruppoSanguigno;
    private String patologieCroniche;
    private BigDecimal pesoKg;
    private String annoLaurea;
    private String durataVisitaMinuti;
    private String numeroAlbo;
    private String specializzazione;
    private String universita;



}
