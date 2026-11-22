package com.development.spring.hGate.H_Gate.dtos.consensi;

import com.development.spring.hGate.H_Gate.enums.TipoConsensoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Un evento dello storico (concessione o revoca). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsensoDTO {
    private Integer id;
    private TipoConsensoEnum tipo;
    private Boolean concesso;
    private String versioneInformativa;
    private LocalDateTime dataEvento;
    private String espressoDa;
    private String note;
}
