package com.development.spring.hGate.H_Gate.components;


import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.libs.data.components.SpecificationFactory;
import com.development.spring.hGate.H_Gate.libs.data.models.SimpleFilterOperator;
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
public class PazienteSpecificationsFactory implements SpecificationFactory<Paziente> {

    private final Logger logger = LoggerFactory.getLogger(PazienteSpecificationsFactory.class);

    private final Set<String> searchableFields = Set.of(
            "codiceFiscale",
            "sesso",
            "allergie",
            "patologieCroniche"
    );

    @Override
    public Specification<Paziente> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Paziente> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case NOT_EQUALS:
                return buildFieldIsNotEqualSpecification(fieldName, value);
            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    @Override
    public <S> Specification<Paziente> buildFieldIsEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsEqualSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Paziente> buildFieldIsNotEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsNotEqualSpecification(fieldName, value);
    }

    @Override
    public Specification<Paziente> buildFieldIsLikeIgnoreCaseSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Paziente> buildSubfieldIsEqualSpecification(String subFieldPath, S value) {
        return SpecificationFactory.super.buildSubfieldIsEqualSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Paziente> buildSubfieldIsLikeIgnoreCaseSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldIsLikeIgnoreCaseSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Paziente> buildSubfieldStartWithSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldStartWithSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Paziente> buildFieldStartWithSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldStartWithSpecification(fieldName, value);
    }

    @Override
    public Specification<Paziente> buildFieldIsNullSpecification(String fieldName) {
        return SpecificationFactory.super.buildFieldIsNullSpecification(fieldName);
    }

    @Override
    public <S> Path<S> getSubFieldPathFromRoot(Root<Paziente> root, String subFieldPath) {
        return SpecificationFactory.super.getSubFieldPathFromRoot(root, subFieldPath);
    }


}
