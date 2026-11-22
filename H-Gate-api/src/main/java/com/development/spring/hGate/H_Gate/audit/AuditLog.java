package com.development.spring.hGate.H_Gate.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * Registro degli accessi ai dati clinici. Le righe non vengono mai modificate né cancellate
 * dall'applicazione ({@link Immutable}): non esistono endpoint di modifica.
 * Utente ed email sono copiati al momento dell'evento, così il registro resta leggibile
 * anche se l'account viene poi modificato o disattivato.
 */
@Data
@Entity
@Immutable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_evento", nullable = false)
    private LocalDateTime dataEvento;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Column(name = "ruoli", length = 100)
    private String ruoli;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AzioneAudit azione;

    @Column(name = "risorsa_tipo", length = 40)
    private String risorsaTipo;

    @Column(name = "risorsa_id")
    private Integer risorsaId;

    @Column(name = "paziente_id")
    private Integer pazienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EsitoAudit esito;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(length = 500)
    private String path;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 300)
    private String userAgent;

    public enum AzioneAudit {
        LETTURA, CREAZIONE, MODIFICA, ELIMINAZIONE, DOWNLOAD,
        LOGIN, LOGIN_FALLITO, LOGIN_BLOCCATO
    }

    public enum EsitoAudit {
        OK, NEGATO, ERRORE
    }
}
