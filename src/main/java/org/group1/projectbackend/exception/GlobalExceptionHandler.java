package org.group1.projectbackend.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Resource not found";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", "Not Found",
                        "message", message
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                        (first, second) -> first
                ));

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Validation Failed",
                        "message", "Request validation failed",
                        "fieldErrors", fieldErrors
                ));
    }
}
