package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pazienti_tutori")
public class PazienteTutore implements IdentifiableEntity<PazienteTutoreId> {

    @EmbeddedId
    private PazienteTutoreId id;

    @ManyToOne
    @MapsId("pazienteId")
    @JoinColumn(name = "paziente_id", nullable = false)
    private Paziente paziente;

    @ManyToOne
    @MapsId("tutoreId")
    @JoinColumn(name="tutore_id", nullable = false)
    private TutoreLegale tutore;

    @Column(nullable = false, length = 50)
    private String relazione;

}
