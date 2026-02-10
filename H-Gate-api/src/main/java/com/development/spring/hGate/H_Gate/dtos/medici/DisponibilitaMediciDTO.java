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

    private Integer giornoSettimana;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private Boolean isAttiva = true;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
