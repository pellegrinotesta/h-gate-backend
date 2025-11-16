package com.development.spring.hGate.H_Gate.libs.data.specifications;

import com.development.spring.hGate.H_Gate.libs.utils.EnumUtils;
import jakarta.persistence.criteria.*;

public abstract class EnumFieldIsLikeSpecification<Entity, EnumType extends Enum<EnumType>, FieldType> implements GenericSpecification<Entity, FieldType> {
    private final Class<EnumType> enumClass;

    private final String value;

    public EnumFieldIsLikeSpecification(Class<EnumType> enumClass, String value) {
        this.enumClass = enumClass;
        this.value = value;
    }

    public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<FieldType> fieldPath = getFieldPath(root);
        return fieldPath.in(EnumUtils.getSimilarEnumValuesIgnoringCase(this.enumClass, this.value));
    }
}
