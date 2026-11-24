package io.github.dtabs17.incident.domain;

import java.time.Instant;
import java.util.Objects;

public abstract sealed class Incident
        permits OutageIncident, DegradationIncident {

    private final String id;
    private final String title;
    private Severity severity;
    private IncidentStatus status;
    private final Instant createdAt;

    protected Incident(String id, String title,
                       Severity severity, Instant createdAt) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Incident ID is required");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Incident title is required");
        }

        this.id = id.strip();
        this.title = title.strip();
        this.severity = Objects.requireNonNull(severity, "Severity is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation time is required");
        this.status = IncidentStatus.OPEN;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Severity getSeverity() {
        return severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String summary() {
        return "%s | %s | %s | %s"
                .formatted(id, title, severity, status);
    }

    public abstract String describeImpact();
}
