package com.development.spring.hGate.H_Gate.shared.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Traduce le eccezioni in risposte HTTP coerenti: status corretto e corpo
 * {@code {"ok": false, "message": "..."}} (stesso formato di {@link ResponseDTO}).
 * <p>
 * I messaggi delle eccezioni "di business" (ResponseStatusException, IllegalArgumentException)
 * sono pensati per l'utente e vengono restituiti; gli errori imprevisti vengono registrati nei log
 * e all'utente arriva un messaggio generico, senza dettagli interni.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    static final String MSG_ERRORE_GENERICO = "Si è verificato un errore imprevisto. Riprova più tardi.";
    static final String MSG_ACCESSO_NEGATO = "Non hai i permessi per accedere a questa risorsa.";
    static final String MSG_NON_AUTENTICATO = "Sessione scaduta o non valida: effettua di nuovo l'accesso.";

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseDTO<Void>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String message = ex.getReason();
        if (status.is5xxServerError()) {
            logger.error("Errore {}: {}", status.value(), message, ex);
        }
        return build(status, message != null ? message : HttpStatus.valueOf(status.value()).getReasonPhrase());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Accesso negato: {} {}", request.getMethod(), request.getRequestURI());
        return build(HttpStatus.FORBIDDEN, MSG_ACCESSO_NEGATO);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO<Void>> handleAuthentication(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, MSG_NON_AUTENTICATO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String dettagli = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "Dati non validi" + (dettagli.isEmpty() ? "" : " (" + dettagli + ")"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDTO<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String dettagli = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "Dati non validi" + (dettagli.isEmpty() ? "" : " (" + dettagli + ")"));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ResponseDTO<Void>> handleBadRequest(Exception ex) {
        logger.debug("Richiesta non valida: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Richiesta non valida: controlla i dati inviati.");
    }

    /** Usate nel codice esistente per errori di validazione con messaggi già scritti per l'utente. */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ResponseDTO<Void>> handleIllegalArgument(RuntimeException ex) {
        String message = ex.getMessage();
        if (message != null && message.toLowerCase().contains("non autorizzato")) {
            return build(HttpStatus.FORBIDDEN, MSG_ACCESSO_NEGATO);
        }
        return build(HttpStatus.BAD_REQUEST, message != null ? message : "Richiesta non valida.");
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<ResponseDTO<Void>> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, "Elemento non trovato.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Risorsa non trovata.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Operazione non supportata.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDTO<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.warn("Violazione di integrità dei dati: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "I dati sono in conflitto con quelli esistenti (es. un valore già registrato).");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMaxUpload(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "File troppo grande (max 10 MB per file).");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
        logger.error("Errore imprevisto su {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, MSG_ERRORE_GENERICO);
    }

    static ResponseEntity<ResponseDTO<Void>> build(HttpStatusCode status, String message) {
        ResponseDTO<Void> body = new ResponseDTO<>();
        body.setOk(false);
        body.setMessage(message);
        return ResponseEntity.status(status).body(body);
    }
}
