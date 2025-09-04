package fr.paulevans.incidents.service;

import fr.paulevans.incidents.dto.IncidentSummaryDto;
import fr.paulevans.incidents.exceptions.IncidentValidationException;
import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.repository.IncidentRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private Validator validator;
    private IncidentService incidentService;

    private Incident validIncident;

    @BeforeEach
    void setup() {
        incidentRepository = Mockito.mock(IncidentRepository.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        incidentService = new IncidentService(incidentRepository, validator);

        validIncident = new Incident(
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
    }

    @Test
    void testGetIncidentById_WhenExists() {
        when(incidentRepository.findById("1")).thenReturn(Optional.of(validIncident));

        Incident result = incidentService.getIncidentById("1");

        assertThat(result).isEqualTo(validIncident);
        verify(incidentRepository).findById("1");
    }

    @Test
    void testGetIncidentById_WhenNotFound() {
        when(incidentRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> incidentService.getIncidentById("999"));
        verify(incidentRepository).findById("999");
    }

    @Test
    void testPatchIncident_UpdatesSimpleField() {
        when(incidentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> updates = Map.of("title", "New Title");

        Incident updated = incidentService.patchIncident(validIncident, updates);

        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getSummary()).isEqualTo(validIncident.getSummary());
        verify(incidentRepository).save(any());
    }

    @Test
    void testPatchIncident_UpdatesNoteById() {
        when(incidentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> updates = Map.of(
                "notes", List.of(
                        Map.of("id", "n1", "author", "author1", "note", "Updated Note", "timestamp", Instant.now().toString())
                )
        );

        Incident updated = incidentService.patchIncident(validIncident, updates);

        assertThat(updated.getNotes()).hasSize(1);
        assertThat(updated.getNotes().get(0).getNote()).isEqualTo("Updated Note");
    }

    @Test
    void testPatchIncident_ValidationError() {
        Map<String, Object> updates = Map.of("title", ""); // Empty title should fail

        assertThrows(IncidentValidationException.class, () ->
                incidentService.patchIncident(validIncident, updates)
        );
    }

    @Test
    void testSaveIncident() {
        when(incidentRepository.save(validIncident)).thenReturn(validIncident);

        Incident result = incidentService.saveIncident(validIncident);

        assertThat(result).isEqualTo(validIncident);
        verify(incidentRepository).save(validIncident);
    }

    @Test
    void testExistsById() {
        when(incidentRepository.existsById("1")).thenReturn(true);

        boolean exists = incidentService.existsById("1");

        assertThat(exists).isTrue();
        verify(incidentRepository).existsById("1");
    }

    @Test
    void testDeleteIncident() {
        doNothing().when(incidentRepository).deleteById("1");

        incidentService.deleteIncident("1");

        verify(incidentRepository).deleteById("1");
    }


    @Test
    void testIncidentSummaryProjection() {
        // Repository returns a projection-like object
        when(incidentRepository.findAllSummaries()).thenReturn(List.of(validIncident));

        List<IncidentSummaryDto> summaries = incidentService.getAllIncidentSummaries();

        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).id()).isEqualTo("1");
        assertThat(summaries.get(0).title()).isEqualTo("Title Example");
        assertThat(summaries.get(0).severity()).isEqualTo("High");
        assertThat(summaries.get(0).status()).isEqualTo("OPEN");
    }
}
