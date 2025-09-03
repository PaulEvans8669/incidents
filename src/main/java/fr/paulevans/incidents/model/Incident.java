package fr.paulevans.incidents.model;

import fr.paulevans.incidents.annotation.ValidEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incidents")
public class Incident {
    @Id
    private String id;
    @NotEmpty()
    private String title;
    @NotEmpty()
    private String summary;          // what the incident is about
    @NotEmpty()
    private String severity;
    @NotNull()
    @ValidEnum(enumClass = IncidentStatus.class)
    private String status;
    @NotEmpty()
    private String createdBy;        // who created the incident
    @NotNull()
    private Instant createdAt;       // when it was created@NotNull
    private Instant updatedAt; // track last update
    private String resolutionNote;   // optional note explaining resolution
    private Instant resolvedAt;      // when it was resolved
    private List<String> tags;




}