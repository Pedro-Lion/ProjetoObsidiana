package com.example.crudObsidiana.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        log.error("HttpMessageNotReadableException: message='{}' causeClass='{}' causeMessage='{}'",
                ex.getMessage(),
                cause != null ? cause.getClass().getName() : "null",
                cause != null ? cause.getMessage() : "null",
                ex);

        // se for InvalidFormatException, extrai detalhes (útil para enums/datas)
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "unknown";
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid value for type " + targetType,
                    "details", ife.getOriginalMessage()
            ));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Corpo inválido ou JSON mal formado", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "rejected", fe.getRejectedValue(),
                        "message", fe.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        log.warn("MethodArgumentNotValidException: {}", errors, ex);
        return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro interno", "message", ex.getMessage()));
    }
}