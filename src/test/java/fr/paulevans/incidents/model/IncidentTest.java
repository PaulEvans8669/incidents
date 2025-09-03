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
                Instant.now(),
                "Event description",
                "user1"
        );

        Incident.Note note = new Incident.Note(
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
    void testInvalidStatus_WhenNotEnum() {
        Incident incident = validIncident();
        incident.setStatus("INVALID");

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("status")
                        && v.getMessage().contains("must be one of"));
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
        incident.setTimeline(List.of(new Incident.TimelineEvent(Instant.now(), null, "actor")));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].description"));
    }

    @Test
    void testInvalidTimelineEvent_WhenEmptyDescription() {
        Incident incident = validIncident();
        incident.setTimeline(List.of(new Incident.TimelineEvent(Instant.now(), "", "actor")));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].description"));
    }

    @Test
    void testInvalidTimelineEvent_WhenNullActor() {
        Incident incident = validIncident();
        incident.setTimeline(List.of(new Incident.TimelineEvent(Instant.now(), "desc", null)));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].actor"));
    }

    @Test
    void testInvalidTimelineEvent_WhenEmptyActor() {
        Incident incident = validIncident();
        incident.setTimeline(List.of(new Incident.TimelineEvent(Instant.now(), "desc", "")));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].actor"));
    }

    @Test
    void testInvalidTimelineEvent_WhenNullTimestamp() {
        Incident incident = validIncident();
        incident.setTimeline(List.of(new Incident.TimelineEvent(null, "desc", "actor")));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("timeline[0].timestamp"));
    }

    // ------------------ NOTES ------------------

    @Test
    void testInvalidNote_WhenNullAuthor() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note(null, "note text", Instant.now())));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("notes[0].author"));
    }

    @Test
    void testInvalidNote_WhenEmptyAuthor() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note("", "note text", Instant.now())));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("notes[0].author"));
    }

    @Test
    void testInvalidNote_WhenNullNoteText() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note("author1", null, Instant.now())));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("notes[0].note"));
    }

    @Test
    void testInvalidNote_WhenEmptyNoteText() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note("author1", "", Instant.now())));

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("notes[0].note"));
    }

    @Test
    void testInvalidNote_WhenNullTimestamp() {
        Incident incident = validIncident();
        incident.setNotes(List.of(new Incident.Note("author1", "text", null)));

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
    void testValidTags_WhenNull() {
        Incident incident = validIncident();
        incident.setTags(null);

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).isEmpty();
    }

    // ------------------ COMBINED INVALID FIELDS ------------------

    @Test
    void testInvalidIncident_MultipleMissingFields() {
        Incident incident = new Incident();
        incident.setStatus("INVALID");

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).hasSizeGreaterThan(5); // title, summary, severity, createdBy, createdAt, status invalid
    }

    @Test
    void testValidIncident_AllFieldsValid() {
        Incident incident = validIncident();

        Set<ConstraintViolation<Incident>> violations = validator.validate(incident);
        assertThat(violations).isEmpty();
    }
}