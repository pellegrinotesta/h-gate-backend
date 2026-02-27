package com.development.spring.hGate.H_Gate.components;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.libs.data.components.SpecificationFactory;
import com.development.spring.hGate.H_Gate.libs.data.models.SimpleFilterOperator;
import jakarta.persistence.criteria.Join;
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
public class MedicoSpecificationsFactory implements SpecificationFactory<Medico>{

    private final Logger logger = LoggerFactory.getLogger(MedicoSpecificationsFactory.class);

    private final Set<String> searchableFields = Set.of(
            "specializzazione",
            "provincia",
            "citta",
            "nomeCompleto"
    );

    @Override
    public Specification<Medico> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Medico> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                if("provincia".equals(fieldName)) {
                    return buildNestedFieldProvincia(value);
                }
                if("citta".equals(fieldName)) {
                    return buildNestedFieldCitta(value);
                }
                if ("nomeCompleto".equals(fieldName)) {
                    return buildNestedFieldNomeCompleto(value);
                }
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

    private Specification<Medico> buildNestedFieldCitta(String filterValue) {
        return (root, query, criteriaBuilder) -> {
            Join<Medico, Users> medicoUsersJoin = root.join("user");
            return criteriaBuilder.like(medicoUsersJoin.get("citta"), "%" + filterValue + "%");
        } ;
    }

    private Specification<Medico> buildNestedFieldProvincia(String filterValue) {
        return (root, query, criteriaBuilder) -> {
            Join<Medico, Users> medicoUsersJoin = root.join("user");
            return criteriaBuilder.like(medicoUsersJoin.get("provincia"), "%" + filterValue + "%");
        } ;
    }

    private Specification<Medico> buildNestedFieldNomeCompleto(String filterValue) {
        return (root, query, criteriaBuilder) -> {
            Join<Medico, Users> join = root.join("user");
            String pattern = "%" + filterValue.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(join.get("nome")), pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(join.get("cognome")), pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.concat(
                                            criteriaBuilder.concat(join.get("nome"), " "),
                                            join.get("cognome")
                                    )
                            ),
                            pattern
                    )
            );
        };
    }

    @Override
    public <S> Specification<Medico> buildFieldIsEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsEqualSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Medico> buildFieldIsNotEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsNotEqualSpecification(fieldName, value);
    }

    @Override
    public Specification<Medico> buildFieldIsLikeIgnoreCaseSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Medico> buildSubfieldIsEqualSpecification(String subFieldPath, S value) {
        return SpecificationFactory.super.buildSubfieldIsEqualSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Medico> buildSubfieldIsLikeIgnoreCaseSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldIsLikeIgnoreCaseSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Medico> buildSubfieldStartWithSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldStartWithSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Medico> buildFieldStartWithSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldStartWithSpecification(fieldName, value);
    }

    @Override
    public Specification<Medico> buildFieldIsNullSpecification(String fieldName) {
        return SpecificationFactory.super.buildFieldIsNullSpecification(fieldName);
    }

    @Override
    public <S> Path<S> getSubFieldPathFromRoot(Root<Medico> root, String subFieldPath) {
        return SpecificationFactory.super.getSubFieldPathFromRoot(root, subFieldPath);
    }
}
