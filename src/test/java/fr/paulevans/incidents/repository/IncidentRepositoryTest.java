package fr.paulevans.incidents.repository;


import fr.paulevans.incidents.model.Incident;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class IncidentRepositoryTest {

    @Value("${spring.profiles.active:default}")
    String activeProfile;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IncidentRepository incidentRepository;

    @BeforeEach
    public void cleanDb() {
        mongoTemplate.getDb().drop();  // Clear DB before each test
    }

    @Test
    public void testSaveIncident() {
        // Create TimelineEvent and Note objects
        Incident.TimelineEvent event0 = new Incident.TimelineEvent(
                Instant.now(),
                "REPORTED",
                "user"
        );
        Incident.TimelineEvent event1 = new Incident.TimelineEvent(
                Instant.now(),
                "Detected high CPU usage",
                "monitoring-system"
        );

        Incident.Note note = new Incident.Note(
                "engineer1",
                "Investigating root cause",
                Instant.now()
        );

        // Create Incident object
        Incident incident = new Incident();
        incident.setTitle("High CPU Usage");
        incident.setSeverity("High");
        incident.setStatus("Open");
        incident.setTimeline(List.of(event0, event1));
        incident.setNotes(List.of(note));
        incident.setTags(List.of("cpu", "performance"));

        // Save to MongoDB
        Incident saved = incidentRepository.save(incident);

        // Retrieve from MongoDB
        Optional<Incident> found = incidentRepository.findById(saved.getId());

        // Assertions
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("High CPU Usage");
        assertThat(found.get().getTimeline()).hasSize(2);
        assertThat(found.get().getNotes()).hasSize(1);
        assertThat(found.get().getTags()).contains("cpu");
    }
}