package com.development.spring.hGate.H_Gate.dtos.medici;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParametriVitaliDTO {
    private Integer pressioneSistolica;
    private Integer pressioneDiastolica;
    private Integer frequenzaCardiaca;
    private Double temperatura;
    private Double peso;
    private Integer altezza;
    private Integer saturazione;
}
