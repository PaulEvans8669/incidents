package fr.paulevans.incidents.controller;


import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.service.IncidentService;
import fr.paulevans.incidents.service.IncidentUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final IncidentService incidentService;

    private final IncidentUpdateService incidentUpdateService;

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
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
        if (!incidentService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Incident incident = incidentService.getIncidentById(id);

        Map<String, Map<String, Object>> changes = new HashMap<>();

        updates.forEach((field, newValue) -> {
            try {
                Field f = Incident.class.getDeclaredField(field);
                f.setAccessible(true);
                Object oldValue = f.get(incident);

                // Only update if different
                if (!Objects.equals(oldValue, newValue)) {
                    f.set(incident, convertValueToFieldType(f, newValue));
                    Map<String, Object> changeEntry = new HashMap<>();
                    changeEntry.put("old", oldValue);
                    changeEntry.put("new", newValue);
                    changes.put(field, changeEntry);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Field " + field + " not found or inaccessible");
            }
        });

        incident.setId(id); // ensure the ID is not changed
        incident.setUpdatedAt(Instant.now());
        incidentUpdateService.saveIncidentUpdate(id, changes);

        return ResponseEntity.ok(incidentService.saveIncident(incident));
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