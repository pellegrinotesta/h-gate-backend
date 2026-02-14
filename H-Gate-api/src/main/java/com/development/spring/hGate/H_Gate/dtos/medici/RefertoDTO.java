package com.development.spring.hGate.H_Gate.dtos.medici;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefertoDTO {

    private String uuid;
    private LocalDateTime dataEmissione;
    private String tipoReferto;
    private String titolo;
    private String anamnesi;
    private String esameObiettivo;
    private String diagnosi;
    private String terapia;
    private String prescrizioni;
    private String noteMediche;
    private String parametriVitali;
    private String esamiRichiesti;
    private LocalDate prossimoControllo;
    private Boolean isFirmato = false;
    private String firmaDigitale;
    private LocalDateTime dataFirma;
    private Boolean isInviatoPaziente = false;
    private LocalDateTime dataInvioPaziente;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
