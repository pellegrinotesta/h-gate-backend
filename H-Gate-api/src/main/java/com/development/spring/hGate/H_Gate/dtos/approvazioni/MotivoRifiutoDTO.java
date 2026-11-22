package com.development.spring.hGate.H_Gate.dtos.approvazioni;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MotivoRifiutoDTO {
    @Size(max = 1000)
    private String motivo;
}
