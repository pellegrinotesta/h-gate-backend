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
@Table(name = "valutazioni_psicologiche")
public class ValutazionePsicologica extends BasicEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_user_id", nullable = false)
    private Users medico;

    @Column(name = "data_valutazione")
    private LocalDateTime dataValutazione;

    @Column(name = "tipo_test", nullable = false)
    private String tipoTest;

    // JSON salvato come stringa, deserializzato dal frontend
    @Column(name = "punteggi", columnDefinition = "JSON")
    private String punteggi;

    @Column(name = "interpretazione", columnDefinition = "TEXT")
    private String interpretazione;
}
