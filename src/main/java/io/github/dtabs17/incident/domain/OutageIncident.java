package io.github.dtabs17.incident.domain;

import java.time.Instant;

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
}
