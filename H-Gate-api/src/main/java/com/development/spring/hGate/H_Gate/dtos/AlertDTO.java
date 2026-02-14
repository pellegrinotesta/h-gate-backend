package com.development.spring.hGate.H_Gate.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {

    private String tipo;
    private String titolo;
    private String messaggio;
    private String priorita;
    private String linkAzione;
    private String testoAzione;
    private String icona;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Boolean dismissible = true;
    private Integer count;
}
