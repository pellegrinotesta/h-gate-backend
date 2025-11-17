package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.enums.StatoPagamentoEnum;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pagamenti")
public class Pagamento extends BasicEntity {

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenotazione_id", nullable = false)
    private Prenotazione prenotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importo;

    @Column(name = "metodo_pagamento", length = 50)
    private String metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPagamentoEnum stato = StatoPagamentoEnum.IN_ATTESA;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_scadenza")
    private LocalDateTime dataScadenza;

    @Column(columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
