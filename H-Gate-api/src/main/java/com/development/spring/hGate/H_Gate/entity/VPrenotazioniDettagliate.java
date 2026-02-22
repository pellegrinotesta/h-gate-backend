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

    @Column(name = "tipo_visita")
    private String tipoVisita;

    @Column(name = "stato")
    private String stato;

    @Column(name = "costo")
    private BigDecimal costo;

    @Column(name = "note_paziente")
    private String notePaziente;

    @Column(name = "note_medico")
    private String noteMedico;

    @Column(name = "is_prima_visita")
    private Boolean isPrimaVisita;

    @Column(name = "is_urgente")
    private Boolean isUrgente;

    @Column(name = "promemoria_inviato")
    private Boolean promemoria_inviato;

    @Column(name = "conferma_inviata")
    private Boolean confermaInviata;

    // Dati Paziente
    @Column(name = "paziente_id")
    private Long pazienteId;

    @Column(name = "paziente_nome_completo")
    private String pazienteNomeCompleto;

    @Column(name = "paziente_nome")
    private String pazienteNome;

    @Column(name = "paziente_cognome")
    private String pazienteCognome;

    @Column(name = "paziente_cf")
    private String pazienteCf;

    @Column(name = "paziente_data_nascita")
    private java.time.LocalDate pazienteDataNascita;

    @Column(name = "paziente_eta")
    private Integer pazienteEta;

    @Column(name = "paziente_sesso")
    private String pazienteSesso;

    @Column(name = "paziente_patologie")
    private String pazientePatologie;

    // Dati Tutore
    @Column(name = "tutore_user_id")
    private Long tutoreUserId;

    @Column(name = "tutore_email")
    private String tutoreEmail;

    @Column(name = "tutore_nome_completo")
    private String tutoreNomeCompleto;

    @Column(name = "tutore_nome")
    private String tutoreNome;

    @Column(name = "tutore_cognome")
    private String tutoreCognome;

    @Column(name = "tutore_telefono")
    private String tutoreTelefono;

    // Dati Medico
    @Column(name = "medico_id")
    private Long medicoId;

    @Column(name = "medico_user_id")
    private Long medicoUserId;

    @Column(name = "medico_nome_completo")
    private String medicoNomeCompleto;

    @Column(name = "medico_nome")
    private String medicoNome;

    @Column(name = "medico_cognome")
    private String medicoCognome;

    @Column(name = "medico_email")
    private String medicoEmail;

    @Column(name = "medico_telefono")
    private String medicoTelefono;

    @Column(name = "medico_specializzazione")
    private String medicoSpecializzazione;

    @Column(name = "medico_numero_albo")
    private String medicoNumeroAlbo;

    @Column(name = "medico_rating")
    private BigDecimal medicoRating;

    @Column(name = "medico_durata_visita")
    private Integer medicoDurataVisita;

    // Flag utili
    @Column(name = "is_futura")
    private Boolean isFutura;

    @Column(name = "is_oggi")
    private Boolean isOggi;

    @Column(name = "giorni_mancanti")
    private Integer giorniMancanti;
}
