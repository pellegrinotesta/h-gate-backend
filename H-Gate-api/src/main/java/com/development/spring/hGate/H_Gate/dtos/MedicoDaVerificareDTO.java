package com.development.spring.hGate.H_Gate.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MedicoDaVerificareDTO {

    private Integer id;
    private String nome;
    private String cognome;
    private String email;
    private String specializzazione;
    private String numeroAlbo;
    private String universita;
    private Integer annoLaurea;
    private Boolean hasDocuments;
    private Date createdAt;
}
