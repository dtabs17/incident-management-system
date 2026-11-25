package io.github.dtabs17.incident.report;

import java.time.Instant;
import java.util.Objects;

public final class IncidentReport {

    private final Instant generatedAt;
    private final String[] lines;

    public IncidentReport(Instant generatedAt, String[] lines) {
        this.generatedAt = Objects.requireNonNull(
                generatedAt, "Report time is required");

        Objects.requireNonNull(lines, "Report lines are required");

        this.lines = lines.clone();

        for (String line : this.lines) {
            if (line == null || line.isBlank()) {
                throw new IllegalArgumentException(
                        "Report lines cannot be null or blank");
            }
        }
    }

    public Instant generatedAt() {
        return generatedAt;
    }

    public String[] lines() {
        return lines.clone();
    }

    public String toText() {
        var text = new StringBuilder("Incident report generated at ")
                .append(generatedAt)
                .append(System.lineSeparator());

        for (String line : lines) {
            text.append(line).append(System.lineSeparator());
        }

        return text.toString();
    }
}
