package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "referti")
public class Referto extends BasicEntity {

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prenotazione_id", unique = true, nullable = false)
    private Prenotazione prenotazione;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @Column(name = "data_emissione")
    private LocalDateTime dataEmissione;

    @NotBlank
    @Column(name = "tipo_referto", nullable = false, length = 100)
    private String tipoReferto;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String anamnesi;

    @Column(name = "esame_obiettivo", columnDefinition = "TEXT")
    private String esameObiettivo;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosi;

    @Column(columnDefinition = "TEXT")
    private String terapia;

    @Column(columnDefinition = "TEXT")
    private String prescrizioni;

    @Column(name = "note_mediche", columnDefinition = "TEXT")
    private String noteMediche;

    @Column(name = "parametri_vitali", columnDefinition = "JSON")
    private String parametriVitali;

    @Column(name = "esami_richiesti", columnDefinition = "TEXT")
    private String esamiRichiesti;

    @Column(name = "prossimo_controllo")
    private LocalDate prossimoControllo;

    @Column(name = "is_firmato")
    private Boolean isFirmato = false;

    @Column(name = "firma_digitale", columnDefinition = "TEXT")
    private String firmaDigitale;

    @Column(name = "data_firma")
    private LocalDateTime dataFirma;

    @Column(name = "is_inviato_paziente")
    private Boolean isInviatoPaziente = false;

    @Column(name = "data_invio_paziente")
    private LocalDateTime dataInvioPaziente;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relazioni
    @OneToMany(mappedBy = "referto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Allegato> allegati = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        if (dataEmissione == null) {
            dataEmissione = LocalDateTime.now();
        }
    }
}
