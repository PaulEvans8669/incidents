package fr.paulevans.incidents.repository;


import fr.paulevans.incidents.model.IncidentSideNote;
import fr.paulevans.incidents.model.IncidentTimelineEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentTimelineEventRepository extends MongoRepository<IncidentTimelineEvent, String> {
    // Add custom query methods here if needed
    List<IncidentTimelineEvent> findByIncidentIdOrderByTimestampDesc(String incidentId);

}
