package com.development.spring.hGate.H_Gate.dtos.medici;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefertoCreateDTO {

    private Integer prenotazioneId;
    private String tipoReferto;
    private String titolo;
    private String anamnesi;
    private String esameObiettivo;
    private String diagnosi;
    private String terapia;
    private String prescrizioni;
    private String noteMediche;
    private Object parametriVitali;
    private String esamiRichiesti;
    private LocalDate prossimoControllo;
}
