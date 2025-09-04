package fr.paulevans.incidents.controller;


import fr.paulevans.incidents.dto.IncidentSummaryDto;
import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.service.IncidentService;
import fr.paulevans.incidents.service.IncidentUpdateService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {

    @Autowired
    private Validator validator;

    private final IncidentService incidentService;

    private final IncidentUpdateService incidentUpdateService;

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/summaries")
    public List<IncidentSummaryDto> getIncidentSummaries() {
        return incidentService.getAllIncidentSummaries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable String id) {
        try {
            Incident incident = incidentService.getIncidentById(id);
            return ResponseEntity.ok(incident);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody Incident incident) {
        // Could add more business validation here
        incident.setCreatedAt(Instant.now());
        Incident savedIncident = incidentService.saveIncident(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIncident);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Incident> patchIncident(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        Incident incident;
        try {
            incident = incidentService.getIncidentById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        Incident updatedIncident = incidentService.patchIncident(incident, updates);
        return ResponseEntity.ok(updatedIncident);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable String id) {
        if (!incidentService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    // Helper to convert JSON value to the proper field type
    private Object convertValueToFieldType(Field field, Object value) {
        Class<?> type = field.getType();
        if (type.isEnum() && value instanceof String) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        if (type == Instant.class && value instanceof String) {
            return Instant.parse((String) value);
        }
        return value; // for String, primitives, etc.
    }
}