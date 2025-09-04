package fr.paulevans.incidents.config;

import fr.paulevans.incidents.exceptions.IncidentValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, Object> body = Map.of(
                "details", ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(body);

    }

    @ExceptionHandler(IncidentValidationException.class)
    public ResponseEntity<Map<String, Object>> handleIncidentValidation(
            IncidentValidationException ex) {

        Map<String, Object> body = Map.of(
                "details", ex.getViolations()
                        .stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(body);
    }
}
