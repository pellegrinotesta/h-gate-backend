package com.development.spring.hGate.H_Gate.libs.data.specifications;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class FieldIsEqualSpecification<E, A> implements GenericSpecification<E, A> {
    protected final A value;

    public FieldIsEqualSpecification(A value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(this.getFieldPath(root), this.value);
    }
}
