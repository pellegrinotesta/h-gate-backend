package com.development.spring.hGate.H_Gate.libs.data.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;


public abstract class FieldHasAtLeastNumberOfMembers<T, M> implements GenericSpecification<T, Collection<M>> {
    protected final Integer value;

    public FieldHasAtLeastNumberOfMembers(Integer value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(this.getFieldPath(root)), this.value);
    }
}
