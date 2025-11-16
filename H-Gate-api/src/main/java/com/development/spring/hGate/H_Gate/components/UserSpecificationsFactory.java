package com.development.spring.hGate.H_Gate.components;

import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.libs.data.components.SpecificationFactory;
import com.development.spring.hGate.H_Gate.libs.data.models.SimpleFilterOperator;
import com.development.spring.hGate.H_Gate.libs.data.specifications.EnumFieldIsLikeSpecification;
import com.development.spring.hGate.H_Gate.libs.data.specifications.FieldIsNotEqualSpecification;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
public class UserSpecificationsFactory implements SpecificationFactory<Users> {

    private final Set<String> searchableFields = Set.of(
            "name",
            "surname",
            "email",
            "roles",
            "id");
    private final Logger logger = LoggerFactory.getLogger(UserSpecificationsFactory.class);

    @Override
    public Specification<Users> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Users> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case NOT_EQUALS:
                return buildFieldIsNotEqualSpecification(fieldName, value);
            case CONTAINS:
                return buildRolesContains(value);
            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    private Specification<Users> buildSubfieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildSubfieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildSubfieldIsLikeIgnoreCaseSpecification(fieldName, value);
            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    @Override
    public <S> Specification<Users> buildFieldIsEqualSpecification(String fieldName, S value) {
        if ("roles".equals(fieldName)) {
            return buildRolesContains((String) value);
        }
        return SpecificationFactory.super.buildFieldIsEqualSpecification(fieldName, value);
    }

    @Override
    public Specification<Users> buildFieldIsLikeIgnoreCaseSpecification(String fieldName, String value) {
        if ("roles".equals(fieldName)) {
            return buildRolesContains(value);
        }
        return SpecificationFactory.super.buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Users> buildFieldIsNotEqualSpecification(String fieldName, S value) {
        if ("id".equals(fieldName)) {
            return buildIdNotEquals(value);
        }
        return SpecificationFactory.super.buildFieldIsNotEqualSpecification(fieldName, value);
    }

    private Specification<Users> buildRolesContains(String value) {
        return new EnumFieldIsLikeSpecification<Users, Role, Set<Role>>(Role.class, value) {
            @Override
            public Path<Set<Role>> getFieldPath(Root<Users> root) {
                return root.join("roles", JoinType.LEFT);
            }
        };
    }

    private <S> Specification<Users> buildIdNotEquals(S value) {
        Long id;
        if (value != null) {
            try {
                id = Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for field id.");
            }
        } else {
            id = null;
        }
        return new FieldIsNotEqualSpecification<>(id) {
            @Override
            public Path<Long> getFieldPath(Root<Users> root) {
                return root.get("id");
            }
        };
    }
}
