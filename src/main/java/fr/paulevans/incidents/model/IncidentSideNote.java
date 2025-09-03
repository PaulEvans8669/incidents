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
@Document(collection = "incident_sidenotes")
public class IncidentSideNote {
    @Id
    private String id;
    @NotEmpty()
    private String author;
    @NotEmpty()
    private String note;
    @NotNull()
    private Instant timestamp;
}