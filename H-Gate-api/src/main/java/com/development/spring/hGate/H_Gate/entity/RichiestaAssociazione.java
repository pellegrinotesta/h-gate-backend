package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.enums.StatoRichiestaEnum;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Richiesta di un tutore di essere associato a un minore già registrato.
 * Viene approvata o rifiutata da un amministratore dopo la verifica dei documenti.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "richieste_associazione")
public class RichiestaAssociazione extends BasicEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutore_id", nullable = false)
    private TutoreLegale tutore;

    @Column(nullable = false, length = 50)
    private String relazione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatoRichiestaEnum stato = StatoRichiestaEnum.IN_ATTESA;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestita_da")
    private Users gestitaDa;

    @Column(name = "data_gestione")
    private LocalDateTime dataGestione;

    @Column(name = "motivo_rifiuto", columnDefinition = "TEXT")
    private String motivoRifiuto;
}
