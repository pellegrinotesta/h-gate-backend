package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioniDettagliateDTO {

    private String uuid;
    private String numeroPrenotazione;
    private Date dataOra;
    private Date dataOraFine;
    private String tipoVisita;
    private String stato;
    private BigDecimal costo;
    private String notePaziente;
    private String noteMedico;
    private Boolean isPrimaVisita;
    private Boolean isUrgente;
    private Boolean promemoria_inviato;
    private Boolean confermaInviata;
    private Long pazienteId;
    private String pazienteNomeCompleto;
    private String pazienteNome;
    private String pazienteCognome;
    private String pazienteCf;
    private LocalDate pazienteDataNascita;
    private Integer pazienteEta;
    private String pazienteSesso;
    private String pazientePatologie;
    private Long tutoreUserId;
    private String tutoreEmail;
    private String tutoreNomeCompleto;
    private String tutoreNome;
    private String tutoreCognome;
    private String tutoreTelefono;
    private Long medicoId;
    private Long medicoUserId;
    private String medicoNomeCompleto;
    private String medicoNome;
    private String medicoCognome;
    private String medicoEmail;
    private String medicoTelefono;
    private String medicoSpecializzazione;
    private String medicoNumeroAlbo;
    private BigDecimal medicoRating;
    private Integer medicoDurataVisita;
    private Boolean isFutura;
    private Boolean isOggi;
    private Integer giorniMancanti;
}
