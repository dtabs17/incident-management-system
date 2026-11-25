package io.github.dtabs17.incident.service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.domain.Severity;

public final class IncidentFilters {

    private IncidentFilters() {
        // Utility class: no instances needed.
    }

    public static Predicate<Incident> bySeverities(Severity... severities) {
        Objects.requireNonNull(severities, "Severities are required");

        if (severities.length == 0) {
            throw new IllegalArgumentException(
                    "Select at least one severity");
        }

        Severity[] copied = severities.clone();

        for (Severity severity : copied) {
            Objects.requireNonNull(severity, "Severity cannot be null");
        }

        var accepted = Set.copyOf(Arrays.asList(copied));

        return incident -> accepted.contains(incident.getSeverity());
    }

    public static Predicate<Incident> byStatus(IncidentStatus status) {
        Objects.requireNonNull(status, "Status is required");

        return incident -> incident.getStatus() == status;
    }

    public static Predicate<Incident> byType(
            Class<? extends Incident> type) {

        Objects.requireNonNull(type, "Incident type is required");

        return type::isInstance;
    }
}
