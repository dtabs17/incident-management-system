package io.github.dtabs17.incident.domain;

import java.time.Instant;

public final class DegradationIncident extends Incident {

    private final int performanceLossPercent;

    public DegradationIncident(String id, String title,
                               Severity severity, int performanceLossPercent) {
        this(id, title, severity, Instant.now(), performanceLossPercent);
    }

    public DegradationIncident(String id, String title,
                               Severity severity, Instant createdAt,
                               int performanceLossPercent) {

        super(id, title, severity, createdAt);

        if (performanceLossPercent < 1 || performanceLossPercent > 99) {
            throw new IllegalArgumentException(
                    "Performance loss must be between 1 and 99 percent");
        }

        this.performanceLossPercent = performanceLossPercent;
    }

    public int getPerformanceLossPercent() {
        return performanceLossPercent;
    }

    @Override
    public String describeImpact() {
        return "Service degraded; estimated performance loss: "
                + performanceLossPercent + "%";
    }

    @Override
    public String summary() {
        return super.summary() + " | " + describeImpact();
    }
}
