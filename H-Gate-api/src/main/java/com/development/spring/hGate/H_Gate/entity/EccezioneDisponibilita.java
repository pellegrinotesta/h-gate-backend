package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eccezioni_disponibilita")
public class EccezioneDisponibilita extends BasicEntity {

    @NotNull
    @Column(name = "data_inizio", nullable = false)
    private LocalTime dataInizio;

    @NotNull
    @Column(name = "data_fine", nullable = false)
    private LocalTime dataFine;

    @Column(length = 200)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "is_ricorrente")
    private String isRicorrente;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
