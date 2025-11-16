package com.development.spring.hGate.H_Gate.libs.data.specifications;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class FieldIsLesserThanSpecification<E, A extends Comparable<A>> implements GenericSpecification<E, A> {
    protected final A value;

    protected final boolean isInclusive;

    public FieldIsLesserThanSpecification(A value, boolean isInclusive) {
        this.value = value;
        this.isInclusive = isInclusive;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return this.isInclusive ? criteriaBuilder.lessThanOrEqualTo(this.getFieldPath(root), this.value) : criteriaBuilder.lessThan(this.getFieldPath(root), this.value);
    }
}
