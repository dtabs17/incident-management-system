package io.github.dtabs17.incident.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import io.github.dtabs17.incident.exception.InvalidIncidentStateException;

public abstract sealed

class Incident
permits OutageIncident, DegradationIncident
{

    private final String id;
    private final String title;
    private Severity severity;
    private IncidentStatus status;
    private final Instant createdAt;

    private Engineer assignee;
    private Instant resolvedAt;

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

    public Optional<Engineer> getAssignee() {
        return Optional.ofNullable(assignee);
    }

    public Optional<Instant> getResolvedAt() {
        return Optional.ofNullable(resolvedAt);
    }

    public void assignTo(Engineer engineer) {
        Objects.requireNonNull(engineer, "Engineer is required");

        if (status != IncidentStatus.OPEN
                && status != IncidentStatus.ASSIGNED) {
            throw new InvalidIncidentStateException(
                    "Cannot assign an engineer while incident is " + status);
        }

        this.assignee = engineer;
        this.status = IncidentStatus.ASSIGNED;
    }

    public void startProgress() {
        if (status != IncidentStatus.ASSIGNED) {
            throw new InvalidIncidentStateException(
                    "Only assigned incidents can start progress; current status: "
                            + status);
        }

        this.status = IncidentStatus.IN_PROGRESS;
    }

    public void resolve(Instant resolutionTime) {
        if (status != IncidentStatus.IN_PROGRESS) {
            throw new InvalidIncidentStateException(
                    "Only in-progress incidents can be resolved; current status: "
                            + status);
        }

        Objects.requireNonNull(resolutionTime, "Resolution time is required");

        if (resolutionTime.isBefore(createdAt)) {
            throw new IllegalArgumentException(
                    "Resolution time cannot be earlier than incident creation");
        }

        this.resolvedAt = resolutionTime;
        this.status = IncidentStatus.RESOLVED;
    }

    public String summary() {
        return "%s | %s | %s | %s"
                .formatted(id, title, severity, status);
    }

    public abstract String describeImpact();
}
