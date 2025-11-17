package com.development.spring.hGate.H_Gate.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRegistrationDTO {

    private String passwordHash;
    private String email;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private Date dataNascita;
    private String telefono;
    private String indirizzo;

}
