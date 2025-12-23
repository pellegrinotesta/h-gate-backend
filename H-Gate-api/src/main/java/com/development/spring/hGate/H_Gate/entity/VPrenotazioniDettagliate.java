package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "v_prenotazioni_dettagliate")
public class VPrenotazioniDettagliate extends BasicEntity {

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "numero_prenotazione")
    private String numeroPrenotazione;

    @Column(name = "data_ora")
    private Date dataOra;

    @Column(name = "data_ora_fine")
    private Date dataOraFine;

    @Column(name="tipo_visita")
    private String tipoVisita;

    @Column(name = "stato")
    private String stato;

    @Column(name="costo")
    private BigDecimal costo;

    @Column(name="paziente_user_id")
    private Integer pazienteUserId;

    @Column(name="paziente_nome")
    private String pazienteNome;

    @Column(name = "paziente_email")
    private String pazienteEmail;

    @Column(name="paziente_cf")
    private String pazienteCf;

    @Column(name="medico_user_id")
    private Integer medicoUserId;

    @Column(name = "medico_nome")
    private String medicoNome;

    @Column(name = "medico_email")
    private String medicoEmail;

    @Column(name = "medico_specializzazione")
    private String medicoSpecializzazione;

    @Column(name = "medico_rating")
    private Double medicoRating;

    @Column(name = "created_at")
    private Date createdAt;
}
