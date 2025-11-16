package com.development.spring.hGate.H_Gate.shared.services;

import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import com.development.spring.hGate.H_Gate.libs.utils.ComparableWrapper;
import com.development.spring.hGate.H_Gate.libs.utils.Pair;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public abstract class BasicService {

    protected static final String CONSTRAINT_VIOLATION = "One or more constraint violations occurred";
    protected static final String USER_DEFINED_SQL_EXCEPTION = "45000";
    protected static final String COULD_NOT_EXECUTE_STATEMENT_ERROR = "HY000";
    protected static final String INVALID_SEARCH_CRITERIA = "Invalid search criteria: %s";
    protected static final String PERMISSION_VIOLATION = "User is not authorized to access this information";
    protected static final String DATES_NOT_CONSISTENT = "Please ensure that the dates fall on weekdays, considering public holidays";
    protected static final String DUM_CANNOT_MODIFY_PERMISSIONS = "User is not authorized to modify resources with GDM as role";
    protected static final String SAVING_ENTITY = "Saving entity: {}";
    protected static final String DELETING_ENTITY = "Deleting entity: {}";
    protected static final String SAVED_ENTITY = "Entity saved successfully";
    protected static final String DELETED_ENTITY = "Entity deleted successfully";
    protected static final String USER_DESCRIPTION = "User : {}";

    private static final String HOURLY_COST_NOT_FOUND =  "Hourly cost for the resource not found.";

    protected final Logger logger;

    public BasicService(Logger logger) {
        this.logger = logger;
    }

    public BasicService(){
        logger = LoggerFactory.getLogger(BasicService.class);
    }

    protected  <T extends IdentifiableEntity<K>,K> T save (CrudRepository<T,K> repository, T entity){
        try {
            logger.info(SAVING_ENTITY, entity.toString());
            entity = repository.save(entity);
            logger.info(SAVED_ENTITY);
            return entity;

        } catch (JpaSystemException ex) {

            String errorMessage = handleJpaException(ex);
            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String errorMessage = handleDataIntegrityViolationException(ex);
            logger.error(errorMessage);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    protected String handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return String.format(CONSTRAINT_VIOLATION) ;
    }

    protected String handleJpaException(JpaSystemException ex){
        Throwable cause = ex.getCause();
        String errorMessage = cause.getMessage();
        String errorState;
        if (cause instanceof GenericJDBCException) {

            errorState = ((GenericJDBCException) cause).getSQLException().getSQLState();

            if (errorState.equals(USER_DEFINED_SQL_EXCEPTION))
                errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
            else if(errorState.equals(COULD_NOT_EXECUTE_STATEMENT_ERROR)){
                errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
            }
        }

        return errorMessage;
    }

    protected  <T extends IdentifiableEntity<K>,K> T getById(CrudRepository<T,K> repository, K id, String msg) {
        Optional<T> entity = repository.findById(id);
        if (entity.isEmpty())
            throw buildEntityWithIdNotFoundException(id, msg);

        return entity.get();
    }

    protected  <T extends IdentifiableEntity<K>,K> void deleteById(CrudRepository<T,K> repository, K id) {
        try {
            logger.info(DELETING_ENTITY, id.toString());
            repository.deleteById(id);
            logger.info(DELETED_ENTITY);

        } catch (JpaSystemException ex) {

            String errorMessage = handleJpaException(ex);
            logger.error(errorMessage);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String errorMessage = handleDataIntegrityViolationException(ex);
            logger.error(errorMessage);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        }
    }

    protected  <T extends IdentifiableEntity<K>,K>  List<T> getAll(CrudRepository<T,K> repository) {
        return repository.findAll();
    }



    protected boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    protected LocalDate getLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    protected Date getDateFromLocalDate(LocalDate date){
        LocalDateTime localDateTime = date.atStartOfDay();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }


    protected <T> Pair<Boolean, String> isSortedOnNonDirectlyMappedField(Map<String, Function<T, ComparableWrapper>> sortingFields, Pageable pageable) {
        for (Sort.Order order : pageable.getSort())
            if(sortingFields.containsKey(order.getProperty()))
                return new Pair<>(true, (order.getProperty()));
        return new Pair<>(false, null);
    }

    protected <T> Page<T> getPage(List<T> context, Pageable pageable){
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int start = Math.min(pageNumber * pageSize, context.size());
        int end = Math.min((pageNumber + 1) * pageSize, context.size());
        return new PageImpl<>(context.subList(start, end), pageable, context.size());
    }

    protected <T> Page<T> getPage(Map<String, Function<T, ComparableWrapper>> sortingFields, List<T> entities, Pageable pageable, String sortingProperty) {

        if(sortingFields.containsKey(sortingProperty))
            return createSortedPage(entities, pageable, sortingProperty, sortingFields.get(sortingProperty));

        return new PageImpl<>(Collections.emptyList());
    }

    protected <T, R extends Comparable<? super R>> Page<T>  createSortedPage(List<T> entities, Pageable pageable, String sortingKey, Function<T, R> keyExtractor) {
        boolean isAscending = getSortingCriteria(sortingKey, pageable);

        Comparator<T> comparator = isAscending ?
                Comparator.comparing(keyExtractor, Comparator.nullsFirst(Comparator.naturalOrder())) :
                Comparator.comparing(keyExtractor, Comparator.nullsLast(Comparator.naturalOrder())).reversed();

        List<T> sortedEntities = entities.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return getPage(sortedEntities,pageable);
    }

    protected boolean getSortingCriteria(String sortingKey, Pageable pageable){
        boolean ascending = true;
        for (Sort.Order order : pageable.getSort())
            if (order.getProperty().equals(sortingKey))
                ascending = order.isAscending();
        return ascending;
    }

    protected <T> Page<T> removeDuplicates(Page<T> page) {

        List<T> uniqueElements = new ArrayList<>();
        for (T t : page.getContent()) {

            if (!uniqueElements.contains(t))
                uniqueElements.add(t);
        }
        return new PageImpl<>(uniqueElements, page.getPageable(), page.getTotalElements());
    }

    protected <K> ResponseStatusException buildEntityWithIdNotFoundException(K id, String msg) {
        String message = String.format(msg, id);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    protected ResponseStatusException advancedSearchWithNoPermissionException() {
        String message = String.format(PERMISSION_VIOLATION);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    protected ResponseStatusException buildDumCannotModifyPermissionsException() {
        String message = String.format(DUM_CANNOT_MODIFY_PERMISSIONS);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    protected ResponseStatusException buildDatesNotConsistentException(){
        String message = String.format(DATES_NOT_CONSISTENT);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    protected ResponseStatusException buildHourlyCostNotFoundException() {
        String message = String.format(HOURLY_COST_NOT_FOUND);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
