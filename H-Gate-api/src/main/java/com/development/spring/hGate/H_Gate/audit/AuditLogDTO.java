package com.development.spring.hGate.H_Gate.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Riga del registro accessi per l'amministratore. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private LocalDateTime dataEvento;
    private Integer userId;
    private String userEmail;
    private String utenteNome;
    private String ruoli;
    private AuditLog.AzioneAudit azione;
    private AuditLog.EsitoAudit esito;
    private String risorsaTipo;
    private Integer risorsaId;
    private Integer pazienteId;
    private String pazienteNome;
    private Integer httpStatus;
    private String httpMethod;
    private String path;
    private String ipAddress;
}
