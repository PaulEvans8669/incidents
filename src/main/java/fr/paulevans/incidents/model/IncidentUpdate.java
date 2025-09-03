package fr.paulevans.incidents.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "incident_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentUpdate {
    @Id
    private String id;
    @NotNull()
    private String incidentId;      // reference to the parent Incident
    @NotNull()
    private Instant timestamp;
    @NotEmpty()
    private String updatedBy;
    @NotEmpty()
    private String field;
    @NotEmpty()
    private Map<String, Map<String, Object>> changes; // key = field, value = { old: ..., new: ... }
}
