package fr.paulevans.incidents.dto;

import fr.paulevans.incidents.model.Incident;
import fr.paulevans.incidents.model.IncidentSideNote;
import fr.paulevans.incidents.model.IncidentTimelineEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.Collections;
import java.util.List;


@Builder
public record IncidentDetailsDto(
        String id,
        String title,
        String summary,
        String severity,
        String status,
        String createdBy,
        Instant createdAt,
        Instant updatedAt,
        String resolutionNote,
        Instant resolvedAt,
        List<String> tags,
        List<IncidentSideNote> sideNotes,
        List<IncidentTimelineEvent> timeline
) {

    // Optional: minimal mapping when you only have the Incident
    public static IncidentDetailsDto from(Incident incident) {
        return from(incident, Collections.emptyList(), Collections.emptyList());
    }
}
