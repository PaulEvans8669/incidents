package fr.paulevans.incidents.repository;


import fr.paulevans.incidents.model.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends MongoRepository<Incident, String> {
    // Add custom query methods here if needed
}
