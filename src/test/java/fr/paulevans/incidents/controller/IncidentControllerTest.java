package fr.paulevans.incidents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.paulevans.incidents.exceptions.IncidentValidationException;
import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IncidentService incidentService;

    private Incident validIncident;

    @BeforeEach
    void setup() {
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
                List.of(new Incident.Note("n1", "author1", "note content", Instant.now())),
                List.of("tag1", "tag2")
        );
    }

    // ------------------ CREATE ------------------

    @Test
    void testCreateIncident_WhenValid_ShouldReturnCreated() throws Exception {
        when(incidentService.saveIncident(any())).thenReturn(validIncident);

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));

        verify(incidentService).saveIncident(any());
    }

    @Test
    void testCreateIncident_WhenTitleEmpty_ShouldReturnBadRequest() throws Exception {
        validIncident.setTitle("");

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@ =~ /title:.*/)]").exists());
    }

    @Test
    void testCreateIncident_WhenSummaryEmpty_ShouldReturnBadRequest() throws Exception {
        validIncident.setSummary("");

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@ =~ /summary:.*/)]").exists());
    }

    @Test
    void testCreateIncident_WhenSeverityEmpty_ShouldReturnBadRequest() throws Exception {
        validIncident.setSeverity("");

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@ =~ /severity:.*/)]").exists());
    }

    @Test
    void testCreateIncident_WhenStatusInvalid_ShouldReturnBadRequest() throws Exception {
        validIncident.setStatus("INVALID");

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@ =~ /status:.*/)]").exists());
    }

    @Test
    void testCreateIncident_WhenCreatedByEmpty_ShouldReturnBadRequest() throws Exception {
        validIncident.setCreatedBy("");

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@ =~ /createdBy:.*/)]").exists());
    }

    @Test
    void testCreateIncident_WhenCreatedAtNull_ShouldReturnBadRequest() throws Exception {
        validIncident.setCreatedAt(null);

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@ =~ /createdAt:.*/)]").exists());
    }

    // ------------------ GET ------------------

    @Test
    void testGetIncident_WhenExists_ShouldReturnOk() throws Exception {
        when(incidentService.getIncidentById("1")).thenReturn(validIncident);

        mockMvc.perform(get("/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(incidentService).getIncidentById("1");
    }

    @Test
    void testGetIncident_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(incidentService.getIncidentById("999"))
                .thenThrow(new NoSuchElementException("Incident not found with id: 999"));

        mockMvc.perform(get("/incidents/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(incidentService).getIncidentById("999");
    }

    // ------------------ LIST ALL ------------------

    @Test
    void testListAllIncidents_ShouldReturnOk() throws Exception {
        when(incidentService.getAllIncidents()).thenReturn(List.of(validIncident));

        mockMvc.perform(get("/incidents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));

        verify(incidentService).getAllIncidents();
    }

    // ------------------ UPDATE / PATCH ------------------

    @Test
    void testUpdateIncident_WhenTitleChanged_ShouldReturnOk() throws Exception {
        Incident updatedIncident = new Incident(
                validIncident.getId(),
                "New Title",
                validIncident.getSummary(),
                validIncident.getSeverity(),
                validIncident.getStatus(),
                validIncident.getCreatedBy(),
                validIncident.getCreatedAt(),
                Instant.now(),
                validIncident.getResolutionNote(),
                validIncident.getResolvedAt(),
                validIncident.getTimeline(),
                validIncident.getNotes(),
                validIncident.getTags()
        );

        when(incidentService.getIncidentById("1")).thenReturn(validIncident);
        when(incidentService.patchIncident(eq(validIncident), any(Map.class)))
                .thenReturn(updatedIncident);

        mockMvc.perform(patch("/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "New Title"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));

        verify(incidentService).patchIncident(eq(validIncident), any(Map.class));
    }

    @Test
    void testUpdateIncident_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
        when(incidentService.getIncidentById("999"))
                .thenThrow(new NoSuchElementException("Incident not found with id: 999"));

        mockMvc.perform(patch("/incidents/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "New Title"))))
                .andExpect(status().isNotFound());

        verify(incidentService).getIncidentById("999");
        verify(incidentService, never()).patchIncident(any(), any());
    }

    @Test
    void testUpdateIncident_WhenStatusInvalid_ShouldReturnBadRequest() throws Exception {
        when(incidentService.getIncidentById("1")).thenReturn(validIncident);
        when(incidentService.patchIncident(eq(validIncident), any(Map.class)))
                .thenThrow(new IncidentValidationException(Set.of()));

        mockMvc.perform(patch("/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "INVALID"))))
                .andExpect(status().isBadRequest());

        verify(incidentService).patchIncident(eq(validIncident), any(Map.class));
    }

    // ------------------ DELETE ------------------

    @Test
    void testDeleteIncident_WhenExists_ShouldReturnNoContent() throws Exception {
        when(incidentService.existsById("1")).thenReturn(true);
        doNothing().when(incidentService).deleteIncident("1");

        mockMvc.perform(delete("/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(incidentService).deleteIncident("1");
    }

    @Test
    void testDeleteIncident_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(incidentService.existsById("999")).thenReturn(false);

        mockMvc.perform(delete("/incidents/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(incidentService, never()).deleteIncident(any());
    }

    // ------------------ TIMELINE BOUNDARY ------------------

    @Test
    void testCreateIncident_WhenTimelineEmpty_ShouldReturnOk() throws Exception {
        validIncident.setTimeline(List.of());

        when(incidentService.saveIncident(any())).thenReturn(validIncident);

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.timeline").isEmpty());
    }

    @Test
    void testCreateIncident_WhenTimelineEventInvalid_ShouldReturnBadRequest() throws Exception {
        validIncident.setTimeline(List.of(new Incident.TimelineEvent(null, null, "", "")));

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists());
    }

    // ------------------ NOTES BOUNDARY ------------------

    @Test
    void testCreateIncident_WhenNotesEmpty_ShouldReturnOk() throws Exception {
        validIncident.setNotes(List.of());

        when(incidentService.saveIncident(any())).thenReturn(validIncident);

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notes").isEmpty());
    }

    @Test
    void testCreateIncident_WhenNoteInvalid_ShouldReturnBadRequest() throws Exception {
        validIncident.setNotes(List.of(new Incident.Note(null, "", "", null)));

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists());
    }

    // ------------------ TAGS BOUNDARY ------------------

    @Test
    void testCreateIncident_WhenTagsEmpty_ShouldReturnOk() throws Exception {
        validIncident.setTags(List.of());

        when(incidentService.saveIncident(any())).thenReturn(validIncident);

        mockMvc.perform(post("/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncident)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags").isEmpty());
    }
}
