package com.lar.customeronboarding.exception.handler;

import com.lar.customeronboarding.exception.custom.CountryNotAllowedException;
import com.lar.customeronboarding.exception.custom.InvalidCredentialsException;
import com.lar.customeronboarding.exception.custom.UsernameAlreadyExistsException;
import com.lar.customeronboarding.exception.error.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INVALID_REQUEST = "Invalid request";
    private static final String RESOURCE_NOT_FOUND = "Resource not found";
    private static final String INTERNAL_SERVER_ERROR = "Internal server error";

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameTaken(UsernameAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), ex, null);
    }

    @ExceptionHandler(CountryNotAllowedException.class)
    public ResponseEntity<ApiError> handleCountryNotAllowed(CountryNotAllowedException ex) {
        return build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), ex, null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        return build(HttpStatus.BAD_REQUEST, INVALID_REQUEST, ex, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, INVALID_REQUEST, ex, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, INVALID_REQUEST, ex, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoRoute(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND, ex, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, ex, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, ex, null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String clientMessage, Exception ex,
                                           Map<String, List<String>> fieldErrors) {
        var traceId = UUID.randomUUID().toString();

        if (status.is5xxServerError()) {
            log.error("[{}] unhandled error", traceId, ex);
        } else {
            log.warn("[{}] {} - {}: {}", traceId, status.value(),
                    ex.getClass().getSimpleName(), ex.getMessage());
        }

        var body = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(clientMessage)
                .traceId(traceId)
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(body);
    }

}
