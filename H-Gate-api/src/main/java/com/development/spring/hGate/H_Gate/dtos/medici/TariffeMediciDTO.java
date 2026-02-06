package com.development.spring.hGate.H_Gate.dtos.medici;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TariffeMediciDTO {

    private Integer id;
    private String tipoVisita;
    private BigDecimal costo;
    private boolean isPrimaVisita;
    private Integer durataMinuti;
    private boolean isAttiva;
    private LocalDateTime createdAt;
}
