package com.development.spring.hGate.H_Gate.entity.identifies;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PazienteTutoreId implements Serializable {

    @Column(name = "paziente_id")
    private Integer pazienteId;

    @Column(name = "tutore_id")
    private Integer tutoreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PazienteTutoreId)) return false;
        PazienteTutoreId that = (PazienteTutoreId) o;
        return Objects.equals(pazienteId, that.pazienteId) &&
                Objects.equals(tutoreId, that.tutoreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pazienteId, tutoreId);
    }
}
