package com.development.spring.hGate.H_Gate.dtos.medici;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DisponibilitaMediciDTO {

    private Integer id;
    private Integer medicoId;
    private Integer giornoSettimana;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private Boolean isAttiva = true;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public String getGiornoNome() {
        if (giornoSettimana == null) return null;

        return switch (giornoSettimana) {
            case 0 -> "Lunedì";
            case 1 -> "Martedì";
            case 2 -> "Mercoledì";
            case 3 -> "Giovedì";
            case 4 -> "Venerdì";
            case 5 -> "Sabato";
            case 6 -> "Domenica";
            default -> "Non valido";
        };
    }

}
