package fr.paulevans.incidents.dto;

import fr.paulevans.incidents.model.Incident;
import lombok.Builder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record IncidentDto(
        String id,
        String title,
        String summary,
        String severity,
        String status,
        String createdBy,
        Instant createdAt,
        String resolutionNote,
        Instant resolvedAt,
        List<String> tags,
        List<NoteDto> notes,
        List<TimelineEventDto> timeline
) {

    // Map from Incident to IncidentDetailsDto
    public static IncidentDto from(Incident incident) {
        if (incident == null) return null;

        List<NoteDto> notes = incident.getNotes() == null
                ? Collections.emptyList()
                : incident.getNotes().stream()
                .map(NoteDto::from)
                .collect(Collectors.toList());

        List<TimelineEventDto> timeline = incident.getTimeline() == null
                ? Collections.emptyList()
                : incident.getTimeline().stream()
                .map(TimelineEventDto::from)
                .collect(Collectors.toList());

        return new IncidentDto(
                incident.getId(),
                incident.getTitle(),
                incident.getSummary(),
                incident.getSeverity(),
                incident.getStatus(),
                incident.getCreatedBy(),
                incident.getCreatedAt(),
                incident.getResolutionNote(),
                incident.getResolvedAt(),
                incident.getTags(),
                notes,
                timeline
        );
    }

    @Builder
    public record NoteDto(
            String author,
            String note,
            Instant timestamp
    ) {
        public static NoteDto from(Incident.Note note) {
            if (note == null) return null;
            return new NoteDto(
                    note.getAuthor(),
                    note.getNote(),
                    note.getTimestamp()
            );
        }
    }

    @Builder
    public record TimelineEventDto(
            Instant timestamp,
            String description,
            String actor
    ) {
        public static TimelineEventDto from(Incident.TimelineEvent event) {
            if (event == null) return null;
            return new TimelineEventDto(
                    event.getTimestamp(),
                    event.getDescription(),
                    event.getActor()
            );
        }
    }
}
