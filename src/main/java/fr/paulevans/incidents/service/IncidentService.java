package fr.paulevans.incidents.service;

import fr.paulevans.incidents.dto.IncidentSummaryDto;
import fr.paulevans.incidents.exceptions.IncidentValidationException;
import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.repository.IncidentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final Validator validator;

    public IncidentService(IncidentRepository incidentRepository, Validator validator) {
        this.incidentRepository = incidentRepository;
        this.validator = validator;
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public List<IncidentSummaryDto> getAllIncidentSummaries() {
        return incidentRepository.findAllSummaries().stream()
                .map(IncidentSummaryDto::from)
                .toList();
    }

    public Incident getIncidentById(String id) {
        return incidentRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public Incident saveIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public boolean existsById(String id) {
        return incidentRepository.existsById(id);
    }

    public void deleteIncident(String id) {
        incidentRepository.deleteById(id);
    }

    /** Single patch method with nested ID-based support */
    public Incident patchIncident(Incident incident, Map<String, Object> updates) {
        updates.forEach((fieldName, newValue) -> {
            try {
                // Handle nested lists separately
                if ("notes".equals(fieldName) && newValue instanceof List<?> notesList) {
                    patchNotes(incident, notesList);
                } else if ("timeline".equals(fieldName) && newValue instanceof List<?> eventsList) {
                    patchTimeline(incident, eventsList);
                } else {
                    Field field = Incident.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object converted = convertValueToFieldType(field, newValue);
                    field.set(incident, converted);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid field: " + fieldName, e);
            }
        });

        // Validate patched object
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        if (!violations.isEmpty()) {
            throw new IncidentValidationException(violations);
        }

        incident.setUpdatedAt(Instant.now());
        return saveIncident(incident);
    }

    /** Patch individual notes by ID */
    private void patchNotes(Incident incident, List<?> notesList) {
        for (Object obj : notesList) {
            if (!(obj instanceof Map<?, ?> noteMap)) continue;

            String id = (String) noteMap.get("id");
            Incident.Note note = incident.getNotes().stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Note not found: " + id));

            noteMap.forEach((k, v) -> {
                try {
                    Field field = Incident.Note.class.getDeclaredField((String) k);
                    field.setAccessible(true);
                    field.set(note, convertValueToFieldType(field, v));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid Note field: " + k, e);
                }
            });
        }
    }

    private void patchTimeline(Incident incident, List<?> eventsList) {
        for (Object obj : eventsList) {
            if (!(obj instanceof Map<?, ?> eventMap)) continue;

            String id = (String) eventMap.get("id");
            Incident.TimelineEvent event = incident.getTimeline().stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("TimelineEvent not found: " + id));

            eventMap.forEach((k, v) -> {
                try {
                    Field field = Incident.TimelineEvent.class.getDeclaredField((String) k);
                    field.setAccessible(true);
                    field.set(event, convertValueToFieldType(field, v));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid TimelineEvent field: " + k, e);
                }
            });
        }
    }

    // ---- Type conversion ----

    private Object convertValueToFieldType(Field field, Object value) {
        if (value == null) return null;
        Class<?> type = field.getType();
        if (type.isEnum() && value instanceof String s) {
            return Enum.valueOf((Class<Enum>) type, s);
        }
        if (type == Instant.class && value instanceof String s) {
            return Instant.parse(s);
        }
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to " + type);
    }
}
