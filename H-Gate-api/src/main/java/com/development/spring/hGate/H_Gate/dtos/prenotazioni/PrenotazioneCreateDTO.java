package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrenotazioneCreateDTO {

    @NotNull(message = "Il paziente è obbligatorio")
    private Integer pazienteId;

    @NotNull(message = "Il medico è obbligatorio")
    private Integer medicoId;

    @NotNull(message = "La data e ora sono obbligatorie")
    @Future(message = "La data deve essere nel futuro")
    private LocalDateTime dataOra;

    @NotBlank(message = "Il tipo di visita è obbligatorio")
    @Size(max = 100, message = "Il tipo di visita non può superare i 100 caratteri")
    private String tipoVisita;

    @Size(max = 500, message = "Le note non possono superare 500 caratteri")
    private String note;

    @DecimalMin(value = "0.0", message = "Il costo non può essere negativo")
    private BigDecimal costo;

    private boolean isPrimaVisita;
}
