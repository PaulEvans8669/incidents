package fr.paulevans.incidents.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_timelineevents")
public class IncidentTimelineEvent {
    @Id
    private String id;
    @NotNull()
    private Instant timestamp;
    @NotEmpty()
    private String description; // what happened
    @NotEmpty()
    private String actor;       // who performed this action
}
