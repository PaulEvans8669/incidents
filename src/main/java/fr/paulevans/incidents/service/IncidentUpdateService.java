package fr.paulevans.incidents.service;

import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.model.IncidentStatus;
import fr.paulevans.incidents.model.IncidentUpdate;
import fr.paulevans.incidents.repository.IncidentRepository;
import fr.paulevans.incidents.repository.IncidentUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IncidentUpdateService {

    private final IncidentUpdateRepository incidentUpdateRepository;

    public List<IncidentUpdate> getAllIncidentUpdatesForIncidentId(String incidentId) {
        return this.incidentUpdateRepository.findAll();
    }

    public Optional<IncidentUpdate> saveIncidentUpdate(String incidentId, Map<String, Map<String, Object>> changes) {

        // Only create an update if there are actual changes
        if (changes.isEmpty()) {
            return Optional.empty(); // no changes
        }

        IncidentUpdate update = new IncidentUpdate();
        update.setIncidentId(incidentId);
        update.setTimestamp(Instant.now());
        update.setChanges(changes);

        return Optional.of(incidentUpdateRepository.save(update));
    }
}