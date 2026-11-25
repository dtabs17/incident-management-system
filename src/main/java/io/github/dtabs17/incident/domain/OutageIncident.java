package io.github.dtabs17.incident.domain;

import java.time.Instant;
import java.time.Duration;

public final class OutageIncident extends Incident {

    private final int affectedUserCount;

    public OutageIncident(String id, String title,
            Severity severity, int affectedUserCount) {
        this(id, title, severity, Instant.now(), affectedUserCount);
    }

    public OutageIncident(String id, String title,
            Severity severity, Instant createdAt,
            int affectedUserCount) {

        super(id, title, severity, createdAt);

        if (affectedUserCount < 1) {
            throw new IllegalArgumentException(
                    "Affected user count must be positive");
        }

        this.affectedUserCount = affectedUserCount;
    }

    public int getAffectedUserCount() {
        return affectedUserCount;
    }

    @Override
    public String describeImpact() {
        return "Service unavailable; affected users: " + affectedUserCount;
    }

    @Override
    public String summary() {
        return super.summary() + " | " + describeImpact();
    }

    @Override
    public Duration getResolutionWindow() {
        return switch (getSeverity()) {
            case CRITICAL -> Duration.ofHours(1);
            case HIGH -> Duration.ofHours(2);
            case MEDIUM -> Duration.ofHours(4);
            case LOW -> Duration.ofHours(8);
        };
    }

}
