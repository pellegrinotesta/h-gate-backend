package com.development.spring.hGate.H_Gate.dtos;

import com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificaDTO {

    private UserDTO user;
    private TipoNotificaEnum tipo;
    private String titolo;
    private String messaggio;
    private String link;
    private Boolean isLetta;
    private LocalDateTime dataLettura;
    private Boolean isInviataEmail;
    private LocalDateTime dataInvioEmail;
    private LocalDateTime createdAt;
}
