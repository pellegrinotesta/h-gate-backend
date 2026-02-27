package com.development.spring.hGate.H_Gate.components;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Referto;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Component
public class RefertoSpecificationsFactory implements SpecificationFactory<Referto> {

    private static final String DATA_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String INVALID_DATA_PARSING = "Parsing must follow this format" + DATA_PATTERN;
    private final Logger logger = LoggerFactory.getLogger(RefertoSpecificationsFactory.class);

    private final Set<String> searchableFields = Set.of(
            "dataEmissione",
            "tipoReferto",
            "nomeMedico"
    );

    @Override
    public Specification<Referto> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Referto> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                if("nomeMedico".equals(fieldName)) {
                    return buildNestedFieldNomeCompleto(value);
                }
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case NOT_EQUALS:
                return buildFieldIsNotEqualSpecification(fieldName, value);
            case IS_DATE_LTS:
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATA_PATTERN);
                    Date date = dateFormat.parse(value);
                    return buildNestedFieldDateLessThanSpecification(fieldName, date);
                } catch (ParseException e) {
                    String message = String.format(INVALID_DATA_PARSING, e.getMessage());
                    logger.debug(message);
                    throw new RuntimeException();
                }
            case IS_DATE_GTS:
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATA_PATTERN);
                    Date date = dateFormat.parse(value);
                    return buildNestedFieldDateGreaterThanSpecification(fieldName, date);
                } catch (ParseException e) {
                    String message = String.format(INVALID_DATA_PARSING, e.getMessage());
                    logger.debug(message);
                    throw new RuntimeException();
                }
            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    private Specification<Referto> buildNestedFieldNomeCompleto(String filterValue) {
        return (root, query, criteriaBuilder) -> {
            Join<Referto, Medico> join = root.join("medico");
            Join<Medico, Users> usersJoin = join.join("user");
            String pattern = "%" + filterValue.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(usersJoin.get("nome")), pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(usersJoin.get("cognome")), pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    criteriaBuilder.concat(
                                            criteriaBuilder.concat(usersJoin.get("nome"), " "),
                                            usersJoin.get("cognome")
                                    )
                            ),
                            pattern
                    )
            );
        };
    }

    private Specification<Referto> buildNestedFieldDateLessThanSpecification(String fieldPath, Date value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return null;
            }
            LocalDateTime localDateTime = value.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            return criteriaBuilder.lessThan(root.get("dataEmissione"), localDateTime);
        };
    }

    private Specification<Referto> buildNestedFieldDateGreaterThanSpecification(String fieldPath, Date value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return null;
            }
            LocalDateTime localDateTime = value.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            return criteriaBuilder.greaterThan(root.get("dataEmissione"), localDateTime);
        };
    }

    @Override
    public <S> Specification<Referto> buildFieldIsEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsEqualSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Referto> buildFieldIsNotEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsNotEqualSpecification(fieldName, value);
    }

    @Override
    public Specification<Referto> buildFieldIsLikeIgnoreCaseSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<Referto> buildSubfieldIsEqualSpecification(String subFieldPath, S value) {
        return SpecificationFactory.super.buildSubfieldIsEqualSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Referto> buildSubfieldIsLikeIgnoreCaseSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldIsLikeIgnoreCaseSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Referto> buildSubfieldStartWithSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldStartWithSpecification(subFieldPath, value);
    }

    @Override
    public Specification<Referto> buildFieldStartWithSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldStartWithSpecification(fieldName, value);
    }

    @Override
    public Specification<Referto> buildFieldIsNullSpecification(String fieldName) {
        return SpecificationFactory.super.buildFieldIsNullSpecification(fieldName);
    }

    @Override
    public <S> Path<S> getSubFieldPathFromRoot(Root<Referto> root, String subFieldPath) {
        return SpecificationFactory.super.getSubFieldPathFromRoot(root, subFieldPath);
    }
}
