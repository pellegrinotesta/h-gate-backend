package com.development.spring.hGate.H_Gate.dtos.approvazioni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicoInAttesaDTO {
    private Integer medicoId;
    private Integer userId;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private String specializzazione;
    private String numeroAlbo;
    private String universita;
    private Integer annoLaurea;
    private Date dataRegistrazione;
}
