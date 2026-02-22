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

    // Campi modificabili dal TUTORE
    @Size(max = 1000, message = "La recensione non può superare 1000 caratteri")
    private String recensione;

    @Size(max = 500, message = "Le note paziente non possono superare 500 caratteri")
    private String notePaziente;

    // Campi modificabili dal MEDICO
    @Size(max = 500, message = "Le note medico non possono superare 500 caratteri")
    private String noteMedico;

    @Size(max = 2000, message = "La diagnosi non può superare 2000 caratteri")
    private String diagnosi;

}
