package fr.paulevans.incidents.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class IncidentTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Incident validIncident() {
        Incident.TimelineEvent event = new Incident.TimelineEvent(
                "t1",
                Instant.now(),
                "Event description",
                "user1"
        );

        Incident.Note note = new Incident.Note(
                "n1",
                "author1",
                "Some note",
                Instant.now()
        );

        return new Incident(
                "1",
                "Valid title",
                "Valid summary",
                "High",
                "OPEN",
                "creator1",
                Instant.now(),
                null,
                "Resolved note",
                Instant.now(),
                List.of(event),
                List.of(note),
                List.of("tag1", "tag2")
        );
    }

    // ------------------ TITLE ------------------

    @Test
    void testInvalidTitle_WhenNull() {
        Incident incident = validIncident();
        incident.setTitle(null);

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    void testInvalidTitle_WhenEmpty() {
        Incident incident = validIncident();
        incident.setTitle("");
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    // ------------------ SUMMARY ------------------

    @Test
    void testInvalidSummary_WhenNull() {
        Incident incident = validIncident();
        incident.setSummary(null);
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("summary"));
    }

    @Test
    void testInvalidSummary_WhenEmpty() {
        Incident incident = validIncident();
        incident.setSummary("");
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("summary"));
    }

    // ------------------ SEVERITY ------------------

    @Test
    void testInvalidSeverity_WhenNull() {
        Incident incident = validIncident();
        incident.setSeverity(null);
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("severity"));
    }

    @Test
    void testInvalidSeverity_WhenEmpty() {
        Incident incident = validIncident();
        incident.setSeverity("");
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("severity"));
    }

    // ------------------ STATUS ------------------

    @Test
    void testInvalidStatus_WhenNull() {
        Incident incident = validIncident();
        incident.setStatus(null);
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }

    @Test
    void testValidStatus_WhenEnumValue() {
        Incident incident = validIncident();
        incident.setStatus("RESOLVED");
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).isEmpty();
    }

    // ------------------ CREATEDBY ------------------

    @Test
    void testInvalidCreatedBy_WhenNull() {
        Incident incident = validIncident();
        incident.setCreatedBy(null);
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("createdBy"));
    }

    @Test
    void testInvalidCreatedBy_WhenEmpty() {
        Incident incident = validIncident();
        incident.setCreatedBy("");
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("createdBy"));
    }

    // ------------------ CREATEDAT ------------------

    @Test
    void testInvalidCreatedAt_WhenNull() {
        Incident incident = validIncident();
        incident.setCreatedAt(null);
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("createdAt"));
    }

    // ------------------ TIMELINE EVENT ------------------

    @Test
    void testInvalidTimelineEvent_WhenNullDescription() {
        Incident incident = validIncident();
        incident.setTimeline(List.of(new Incident.TimelineEvent("t1", Instant.now(), null, "actor")));
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].description"));
    }

    @Test
    void testInvalidTimelineEvent_WhenEmptyActor() {
        Incident incident = validIncident();
        incident.setTimeline(List.of(new Incident.TimelineEvent("t1", Instant.now(), "desc", "")));
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].actor"));
    }

    // ------------------ NOTES ------------------

    @Test
    void testInvalidNote_WhenEmptyAuthor() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note("n1", "", "note text", Instant.now())));
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("notes[0].author"));
    }

    @Test
    void testInvalidNote_WhenNullTimestamp() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note("n1", "author1", "text", null)));
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("notes[0].timestamp"));
    }

    // ------------------ TAGS ------------------

    @Test
    void testValidTags_WhenEmptyList() {
        Incident incident = validIncident();
        incident.setTags(List.of());
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidIncident_AllFieldsValid() {
        Incident incident = validIncident();
        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).isEmpty();
    }
}
