package com.development.spring.hGate.H_Gate.dtos.approvazioni;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApprovazioniConteggioDTO {
    private long mediciInAttesa;
    private long associazioniInAttesa;
}
