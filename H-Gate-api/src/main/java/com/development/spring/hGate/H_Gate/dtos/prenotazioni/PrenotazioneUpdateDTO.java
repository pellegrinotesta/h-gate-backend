package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioneUpdateDTO {

    @NotNull(message = "L'ID è obbligatorio")
    private Integer id;

    @Future(message = "La data deve essere nel futuro")
    private LocalDateTime dataOra;

    @Size(max = 100, message = "Il tipo di visita non può superare 100 caratteri")
    private String tipoVisita;

    @Size(max = 500, message = "Le note non possono superare 500 caratteri")
    private String note;

}
