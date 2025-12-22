package com.development.spring.hGate.H_Gate.dtos;

import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private PazienteDTO paziente;
    private MedicoDTO medico;
    private LocalDateTime dataOra;
    private LocalDateTime dataOraFine;
    private String tipoVisita;
    private StatoPrenotazioneEnum stato = StatoPrenotazioneEnum.IN_ATTESA;
    private BigDecimal costo;
    private String noteMedico;
    private String motivoAnnullamento;
    private UserDTO annullataDa;
    private LocalDateTime dataAnnullamento;
    private Boolean promemoriaInviato = false;
    private Boolean confermaInviata = false;
    private Boolean isPrimaVisita = false;
    private Boolean isUrgente = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RefertoDTO referto;
//    private RecensioneDTO recensione;
//    private List<PagamentoDTO> pagamenti = new ArrayList<>();
}
