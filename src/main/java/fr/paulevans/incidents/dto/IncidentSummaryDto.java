package fr.paulevans.incidents.dto;

import fr.paulevans.incidents.model.Incident;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record IncidentSummaryDto(
        String id,
        String title,
        String summary,
        String severity,
        String status,
        String createdBy,
        Instant createdAt,
        String resolutionNote,
        Instant resolvedAt,
        List<String> tags
) {

    public static IncidentSummaryDto from(Incident incident) {
        if (incident == null) return null;

        return new IncidentSummaryDto(
                incident.getId(),
                incident.getTitle(),
                incident.getSummary(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getCreatedBy(),
                incident.getCreatedAt(),
                incident.getResolutionNote(),
                incident.getResolvedAt(),
                incident.getTags()
        );
    }
}
