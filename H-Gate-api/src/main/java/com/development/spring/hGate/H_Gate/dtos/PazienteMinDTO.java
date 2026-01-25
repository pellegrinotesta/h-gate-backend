package com.development.spring.hGate.H_Gate.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PazienteMinDTO {

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    private String codiceFiscale;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    private Date dataNascita;

    @NotBlank(message = "Il sesso è obbligatorio")
    private String sesso;

    @NotBlank(message = "La relazione con il minore è obbligatoria")
    private String relazione;

    // Campi opzionali sanitari
    private String gruppoSanguigno;
    private BigDecimal pesoKg;
    private Integer altezzaCm;
    private String allergie;
    private String patologieCroniche;
    private String noteMediche;
    private String email;
}