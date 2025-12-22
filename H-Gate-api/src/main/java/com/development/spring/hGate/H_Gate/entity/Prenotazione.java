package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prenotazioni")
public class Prenotazione extends BasicEntity {


    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(name = "numero_prenotazione", unique = true, nullable = false, length = 20)
    private String numeroPrenotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @NotNull
    @Column(name = "data_ora", nullable = false)
    private Date dataOra;

    @NotNull
    @Column(name = "data_ora_fine", nullable = false)
    private Date dataOraFine;

    @NotBlank
    @Column(name = "tipo_visita", nullable = false, length = 100)
    private String tipoVisita;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPrenotazioneEnum stato = StatoPrenotazioneEnum.IN_ATTESA;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "note_paziente", columnDefinition = "TEXT")
    private String notePaziente;

    @Column(name = "note_medico", columnDefinition = "TEXT")
    private String noteMedico;

    @Column(name = "motivo_annullamento", columnDefinition = "TEXT")
    private String motivoAnnullamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annullata_da")
    private Users annullataDa;

    @Column(name = "data_annullamento")
    private LocalDateTime dataAnnullamento;

    @Column(name = "promemoria_inviato")
    private Boolean promemoriaInviato = false;

    @Column(name = "conferma_inviata")
    private Boolean confermaInviata = false;

    @Column(name = "is_prima_visita")
    private Boolean isPrimaVisita = false;

    @Column(name = "is_urgente")
    private Boolean isUrgente = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relazioni
    @OneToOne(mappedBy = "prenotazione", cascade = CascadeType.ALL)
    @JsonIgnore
    private Referto referto;

    @OneToOne(mappedBy = "prenotazione", cascade = CascadeType.ALL)
    @JsonIgnore
    private Recensione recensione;

    @OneToMany(mappedBy = "prenotazione", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pagamento> pagamenti = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
