package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.enums.TipoConsensoEnum;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Storico dei consensi: ogni concessione o revoca è una nuova riga, mai modificata.
 * Lo stato attuale di un consenso è l'ultima riga per (paziente, tipo).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consensi")
public class Consenso extends BasicEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    /** Chi ha espresso il consenso (tutore) o registrato la revoca. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoConsensoEnum tipo;

    @Column(nullable = false)
    private Boolean concesso;

    @Column(name = "versione_informativa", nullable = false, length = 30)
    private String versioneInformativa;

    @Column(name = "data_evento", nullable = false)
    private LocalDateTime dataEvento;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String note;
}
