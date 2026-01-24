package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString(exclude = {"prenotazioni", "referti", "recensioni", "medicoBase"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pazienti")
public class Paziente extends BasicEntity {

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "sesso")
    private String sesso;

    @Column(name="data_nascita")
    private Date dataNascita;

    @Column(name="citta")
    private String citta;

    @Column(name = "codice_fiscale", unique = true, nullable = false, length = 16)
    @Size(min = 16, max = 16)
    private String codiceFiscale;

    @Column(name = "gruppo_sanguigno", length = 3)
    private String gruppoSanguigno;

    @Column(name = "altezza_cm")
    @Min(50)
    @Max(250)
    private Integer altezzaCm;

    @Column(name = "peso_kg", precision = 5, scale = 2)
    @DecimalMin("2.0")
    @DecimalMax("300.0")
    private BigDecimal pesoKg;

    @Column(columnDefinition = "TEXT")
    private String allergie;

    @Column(name = "patologie_croniche", columnDefinition = "TEXT")
    private String patologieCroniche;

    @Column(name = "note_mediche", columnDefinition = "TEXT")
    private String noteMediche;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_base_id")
    private Medico medicoBase;

    @Column(name = "consenso_privacy")
    private Boolean consensoPrivacy = false;

    @Column(name = "consenso_marketing")
    private Boolean consensoMarketing = false;

    @Column(name = "data_consenso")
    private LocalDateTime dataConsenso;

    // Relazioni
    @OneToMany(mappedBy = "paziente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    @OneToMany(mappedBy = "paziente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Referto> referti = new ArrayList<>();

    @OneToMany(mappedBy = "paziente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Recensione> recensioni = new ArrayList<>();



}
