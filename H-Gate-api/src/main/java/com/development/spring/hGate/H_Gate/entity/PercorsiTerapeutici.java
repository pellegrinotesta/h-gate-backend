package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "percorsi_terapeutici")
public class PercorsiTerapeutici extends BasicEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_user_id", nullable = false)
    private Users medico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @Column(name = "titolo")
    private String titolo;

    @Column(name = "obiettivi")
    private String obiettivi;

    @Column(name = "data_inizio")
    private LocalDateTime dataInizio;

    @Column(name = "data_fine_prevista")
    private LocalDateTime dataFinePrevista;

    @Column(name = "stato")
    private String stato;

    @Column(name = "numero_sedute_previste")
    private Integer numeroSedutePreviste;

    @Column(name = "numero_sedute_effettuate")
    private Integer numeroSeduteEffettuate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
