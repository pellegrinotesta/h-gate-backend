package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recensioni")
public class Recensione extends BasicEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenotazione_id", unique = true, nullable = false)
    private Prenotazione prenotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 200)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String commento;

    @Column(name = "is_anonima")
    private Boolean isAnonima = false;

    @Column(name = "is_verificata")
    private Boolean isVerificata = false;

    @Column(name = "is_pubblicata")
    private Boolean isPubblicata = true;

    @Column(name = "risposta_medico", columnDefinition = "TEXT")
    private String rispostaMedico;

    @Column(name = "data_risposta")
    private LocalDateTime dataRisposta;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
