package com.development.spring.hGate.H_Gate.libs.data.specifications;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class FieldStartWithSubstringSpecification<E> implements GenericSpecification<E, String> {
    protected final String value;

    public FieldStartWithSubstringSpecification(String value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.lower(this.getFieldPath(root)), (this.value + "%").toLowerCase());
    }
}
