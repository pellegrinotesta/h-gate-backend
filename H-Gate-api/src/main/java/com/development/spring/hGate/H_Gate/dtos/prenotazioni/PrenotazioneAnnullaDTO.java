package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioneAnnullaDTO {

    @NotNull(message = "L'ID è obbligatorio")
    private Integer prenotazioneId;

    @NotBlank(message = "Il motivo dell'annullamento è obbligatorio")
    @Size(max = 500, message = "Il motivo non può superare 500 caratteri")
    private String motivo;
}
