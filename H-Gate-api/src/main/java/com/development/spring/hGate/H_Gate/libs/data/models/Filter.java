package com.development.spring.hGate.H_Gate.libs.data.models;

import com.development.spring.hGate.H_Gate.libs.data.components.SpecificationFactory;
import org.springframework.data.jpa.domain.Specification;

public interface Filter<T> {
    Specification<T> toSpecification(SpecificationFactory<T> paramSpecificationFactory);
    boolean containsField(String field);
}