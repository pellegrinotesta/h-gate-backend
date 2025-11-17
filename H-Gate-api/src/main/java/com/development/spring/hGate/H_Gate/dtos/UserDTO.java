package com.development.spring.hGate.H_Gate.dtos;

import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO {

    @EqualsAndHashCode.Include
    private Integer id;

    private String uuid;
    private String passwordHash;
    private String email;
    private String nome;
    private String cognome;
    private String telefono;
    private Date dataNascita;
    private String indirizzo;
    private String citta;
    private String provincia;
    private String cap;
    private boolean isActive;
    private boolean isVerified;
    private Date ultimoAccesso;
    private Date createdAt;
    private Date updatedAt;

    private Set<Role> roles;

}
