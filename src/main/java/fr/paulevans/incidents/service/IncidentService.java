package fr.paulevans.incidents.service;

import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Incident getIncidentById(String id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found with id: " + id));
    }

    public Incident saveIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public void deleteIncident(String id) {
        if (!existsById(id)) {
            throw new NoSuchElementException("Incident not found with id: " + id);
        }
        incidentRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return incidentRepository.existsById(id);
    }
}