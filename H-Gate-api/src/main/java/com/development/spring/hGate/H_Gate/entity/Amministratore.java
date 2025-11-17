package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "amministratori")
public class Amministratore extends BasicEntity {

    @Column(name = "livello_accesso")
    @Min(1)
    @Max(10)
    private Integer livelloAccesso = 1;

    @Column(length = 100)
    private String dipartimento;

    @Column(columnDefinition = "JSON")
    private String permessi;

    @Column(columnDefinition = "TEXT")
    private String note;

}
