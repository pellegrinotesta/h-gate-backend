package com.development.spring.hGate.H_Gate.libs.data.specifications;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;


public abstract class FieldHasMemberSpecification<E, M> implements GenericSpecification<E, Collection<M>> {
    protected final M member;

    public FieldHasMemberSpecification(M member) {
        this.member = member;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isMember(this.member, this.getFieldPath(root));
    }
}