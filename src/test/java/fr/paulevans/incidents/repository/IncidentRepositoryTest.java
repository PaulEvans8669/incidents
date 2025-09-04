package fr.paulevans.incidents.repository;

import fr.paulevans.incidents.dto.IncidentSummaryDto;
import fr.paulevans.incidents.model.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataMongoTest
class IncidentRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    private Incident incident;

    @BeforeEach
    void setup() {
        incident = new Incident(
                "1",
                "Title Example",
                "Summary Example",
                "High",
                "OPEN",
                "creator1",
                Instant.now(),
                null,
                "Resolution Note",
                Instant.now(),
                List.of(new Incident.TimelineEvent("t1", Instant.now(), "Created", "user1")),
                List.of(new Incident.Note("n1", "author1", "Note content", Instant.now())),
                List.of("tag1", "tag2")
        );
        incidentRepository.save(incident);
    }

    @Test
    void testSaveIncident() {
        Incident saved = incidentRepository.save(
                new Incident(
                        "2",
                        "Another Title",
                        "Another Summary",
                        "Medium",
                        "OPEN",
                        "creator2",
                        Instant.now(),
                        null,
                        null,
                        null,
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        assertThat(saved.getId()).isEqualTo("2");
        assertThat(incidentRepository.findById("2")).isPresent();
    }

    @Test
    void testFindById() {
        Optional<Incident> found = incidentRepository.findById("1");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Title Example");
    }

    @Test
    void testFindAll() {
        List<Incident> incidents = incidentRepository.findAll();
        assertThat(incidents).hasSize(1);
    }

    @Test
    void testDeleteIncident() {
        incidentRepository.deleteById("1");
        assertThat(incidentRepository.findById("1")).isEmpty();
    }

    @Test
    void testExistsById() {
        assertThat(incidentRepository.existsById("1")).isTrue();
        assertThat(incidentRepository.existsById("999")).isFalse();
    }

    @Test
    void testUpdateIncident() {
        incident.setTitle("Updated Title");
        Incident updated = incidentRepository.save(incident);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        Optional<Incident> found = incidentRepository.findById("1");
        assertThat(found.get().getTitle()).isEqualTo("Updated Title");
    }
}
