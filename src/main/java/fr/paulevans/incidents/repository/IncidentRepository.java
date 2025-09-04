package fr.paulevans.incidents.repository;

import fr.paulevans.incidents.dto.IncidentSummaryDto;
import fr.paulevans.incidents.model.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends MongoRepository<Incident, String> {

    // Use a projection query to return only the fields needed for the summary
    @Query(value = "{}", fields = "{ 'id': 1, 'title': 1, 'summary': 1, 'severity': 1, 'status': 1, 'createdBy': 1, 'createdAt': 1, 'resolutionNote': 1, 'resolvedAt': 1, 'tags': 1 }")
    List<Incident> findAllSummaries();
}
