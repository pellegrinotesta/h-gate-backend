package com.development.spring.hGate.H_Gate.dtos.consensi;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RevocaConsensoDTO {
    @Size(max = 1000)
    private String motivo;
}
