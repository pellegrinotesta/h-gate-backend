package com.development.spring.hGate.H_Gate.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Accesso ai dati di un minore, come lo vede il tutore (senza dettagli tecnici). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessoPazienteDTO {
    private LocalDateTime dataEvento;
    private String utente;
    private String ruolo;
    private AuditLog.AzioneAudit azione;
    private String risorsaTipo;
    private AuditLog.EsitoAudit esito;
}
