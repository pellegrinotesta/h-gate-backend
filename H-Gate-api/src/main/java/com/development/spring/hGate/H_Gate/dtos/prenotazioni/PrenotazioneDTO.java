package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoDTO;
import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PrenotazioneDTO {

    private String uuid;
    private String numeroPrenotazione;
    private PazienteMinDTO paziente;
    private MedicoDTO medico;
    private Date dataOra;
    private Date dataOraFine;
    private String tipoVisita;
    private String stato;
    private BigDecimal costo;
    private String notePaziente;
    private String noteMedico;
    private String motivoAnnullamento;
    private UserDTO annullataDa;
    private Date dataAnnullamento;
    private Boolean promemoriaInviato = false;
    private Boolean confermaInviata = false;
    private Boolean isPrimaVisita = false;
    private Boolean isUrgente = false;
    private Date createdAt;
    private Date updatedAt;
    private RefertoDTO referto;
//    private RecensioneDTO recensione;
//    private List<PagamentoDTO> pagamenti = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PazienteMinDTO {

        @NotBlank(message = "Il nome è obbligatorio")
        private String nome;

        @NotBlank(message = "Il cognome è obbligatorio")
        private String cognome;

        @NotBlank(message = "Il codice fiscale è obbligatorio")
        private String codiceFiscale;

        @NotNull(message = "La data di nascita è obbligatoria")
        @Past(message = "La data di nascita deve essere nel passato")
        private Date dataNascita;

        @NotBlank(message = "Il sesso è obbligatorio")
        private String sesso;

        @NotBlank(message = "La relazione con il minore è obbligatoria")
        private String relazione;

        // Campi opzionali sanitari
        private String gruppoSanguigno;
        private BigDecimal pesoKg;
        private Integer altezzaCm;
        private String allergie;
        private String patologieCroniche;
        private String noteMediche;
        private String email;
    }
}
