package com.development.spring.hGate.H_Gate.libs.data.specifications;
import jakarta.persistence.criteria.*;

import java.util.Collection;

public abstract class FieldHasMembersSpecification<E, M> implements GenericSpecification<E, Collection<M>> {
    protected final Collection<M> members;

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        criteriaQuery = criteriaQuery.groupBy(new Expression[]{this.getIdPath(root)});
        criteriaQuery = criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.count(this.getIdPath(root)), this.members.size()));
        criteriaQuery = criteriaQuery.where(this.getFieldPath(root).in(this.members));
        return criteriaQuery.getRestriction();
    }

    protected abstract Path<?> getIdPath(Root<E> root);

    public FieldHasMembersSpecification(final Collection<M> members) {
        this.members = members;
    }
}