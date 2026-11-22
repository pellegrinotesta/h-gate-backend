package com.development.spring.hGate.H_Gate.dtos.approvazioni;

import com.development.spring.hGate.H_Gate.enums.StatoRichiestaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaAssociazioneDTO {
    private Integer id;
    private StatoRichiestaEnum stato;
    private String relazione;
    private LocalDateTime createdAt;
    private LocalDateTime dataGestione;
    private String motivoRifiuto;

    // Chi chiede l'associazione
    private Integer tutoreUserId;
    private String tutoreNome;
    private String tutoreCognome;
    private String tutoreEmail;
    private String tutoreTelefono;

    // Il minore
    private Integer pazienteId;
    private String pazienteNome;
    private String pazienteCognome;
    private String pazienteCodiceFiscale;
    private Date pazienteDataNascita;

    /** Tutori già associati al minore, es. "Anna Verdi (MADRE)": utile per la verifica. */
    private List<String> tutoriAttuali;
}
