package fr.paulevans.incidents.exceptions;


import fr.paulevans.incidents.model.Incident;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class IncidentValidationException extends RuntimeException {

    private final Set<ConstraintViolation<Incident>> violations;

    public IncidentValidationException(Set<ConstraintViolation<Incident>> violations) {
        super("Incident validation failed");
        this.violations = violations;
    }

    public Set<ConstraintViolation<Incident>> getViolations() {
        return violations;
    }
}
