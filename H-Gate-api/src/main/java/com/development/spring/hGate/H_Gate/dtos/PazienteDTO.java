package com.development.spring.hGate.H_Gate.dtos;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.enums.GruppoSanguignoEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PazienteDTO {

    private UserDTO user;
    private GruppoSanguignoEnum gruppoSanguigno;
    private Integer altezzaCm;
    private BigDecimal pesoKg;
    private String allergie;
    private String patologieCroniche;
    private String noteMediche;
    private Medico medicoBase;
    private Boolean consensoPrivacy;
    private Boolean consensoMarketing;
    private Date dataConsenso;
}
