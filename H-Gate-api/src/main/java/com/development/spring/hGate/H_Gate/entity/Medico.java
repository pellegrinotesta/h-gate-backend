package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"recensioni", "disponibilita", "prenotazioni", "referti"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medici")
public class Medico extends BasicEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String specializzazione;

    @Column(name = "numero_albo", unique = true, nullable = false, length = 50)
    private String numeroAlbo;

    @Column(length = 200)
    private String universita;

    @Column(name = "anno_laurea")
    @Min(1950)
    private Integer annoLaurea;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String curriculum;

    @Column(name = "durata_visita_minuti")
    private Integer durataVisitaMinuti = 30;

    @Column(name = "pausa_tra_visite_minuti")
    private Integer pausaTraVisiteMinuti = 5;

    @Column(name = "is_disponibile")
    private Boolean isDisponibile = true;


    @Column(name = "rating_medio", precision = 3, scale = 2)
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private BigDecimal ratingMedio = BigDecimal.ZERO;

    // Relazioni
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Referto> referti = new ArrayList<>();

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DisponibilitaMedico> disponibilita = new ArrayList<>();


}