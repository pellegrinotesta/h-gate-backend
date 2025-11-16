package com.development.spring.hGate.H_Gate.shared.entities;

import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BasicEntity implements IdentifiableEntity<Long> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

}
