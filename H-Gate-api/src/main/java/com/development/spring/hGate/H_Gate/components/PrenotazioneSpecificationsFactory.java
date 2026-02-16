package com.development.spring.hGate.H_Gate.components;

import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
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
public class PrenotazioneSpecificationsFactory implements SpecificationFactory<VPrenotazioniDettagliate> {

    private final Logger logger = LoggerFactory.getLogger(PrenotazioneSpecificationsFactory.class);

    private final Set<String> searchableFields = Set.of(
            "numeroPrenotazione",
            "tipoVisita",
            "stato",
            "pazienteNomeCompleto",
            "tutoreNomeCompleto",
            "medicoNomeCompleto"
    );

    @Override
    public Specification<VPrenotazioniDettagliate> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<VPrenotazioniDettagliate> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
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
    public <S> Specification<VPrenotazioniDettagliate> buildFieldIsEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsEqualSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<VPrenotazioniDettagliate> buildFieldIsNotEqualSpecification(String fieldName, S value) {
        return SpecificationFactory.super.buildFieldIsNotEqualSpecification(fieldName, value);
    }

    @Override
    public Specification<VPrenotazioniDettagliate> buildFieldIsLikeIgnoreCaseSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<VPrenotazioniDettagliate> buildSubfieldIsEqualSpecification(String subFieldPath, S value) {
        return SpecificationFactory.super.buildSubfieldIsEqualSpecification(subFieldPath, value);
    }

    @Override
    public Specification<VPrenotazioniDettagliate> buildSubfieldIsLikeIgnoreCaseSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldIsLikeIgnoreCaseSpecification(subFieldPath, value);
    }

    @Override
    public Specification<VPrenotazioniDettagliate> buildSubfieldStartWithSpecification(String subFieldPath, String value) {
        return SpecificationFactory.super.buildSubfieldStartWithSpecification(subFieldPath, value);
    }

    @Override
    public Specification<VPrenotazioniDettagliate> buildFieldStartWithSpecification(String fieldName, String value) {
        return SpecificationFactory.super.buildFieldStartWithSpecification(fieldName, value);
    }

    @Override
    public Specification<VPrenotazioniDettagliate> buildFieldIsNullSpecification(String fieldName) {
        return SpecificationFactory.super.buildFieldIsNullSpecification(fieldName);
    }

    @Override
    public <S> Path<S> getSubFieldPathFromRoot(Root<VPrenotazioniDettagliate> root, String subFieldPath) {
        return SpecificationFactory.super.getSubFieldPathFromRoot(root, subFieldPath);
    }

}
