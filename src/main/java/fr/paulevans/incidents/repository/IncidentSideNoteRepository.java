package fr.paulevans.incidents.repository;


import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.model.IncidentSideNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentSideNoteRepository extends MongoRepository<IncidentSideNote, String> {
    // Add custom query methods here if needed
    List<IncidentSideNote> findByIncidentIdOrderByTimestampDesc(String incidentId);

}
