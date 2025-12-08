package com.development.spring.hGate.H_Gate.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
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

    private Integer id;
    private String codiceFiscale;
    private String gruppoSanguigno;
    private Integer altezzaCm;
    private BigDecimal pesoKg;
    private String allergie;
    private String patologieCroniche;
    private String noteMediche;
    private Boolean consensoPrivacy;
    private Boolean consensoMarketing;
    private Date dataConsenso;
}
