package fr.paulevans.incidents.repository;

import fr.paulevans.incidents.model.IncidentUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentUpdateRepository extends MongoRepository<IncidentUpdate, String> {
    List<IncidentUpdate> findByIncidentId(String incidentId);
}