package com.development.spring.hGate.H_Gate.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
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
}
