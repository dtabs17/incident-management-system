package io.github.dtabs17.incident.domain;

import java.util.Objects;

public final class IncidentImpactClassifier {

    private IncidentImpactClassifier() {
    }

    public static String classify(Incident incident) {
        Objects.requireNonNull(incident, "Incident is required");

        return switch (incident) {
            case OutageIncident outage ->
                switch (outage.getAffectedUserCount()) {
                    case int count when count >= 500 -> "WIDESPREAD OUTAGE";
                    case int count when count >= 100 -> "SIGNIFICANT OUTAGE";
                    case int count -> "LOCAL OUTAGE";
                };

            case DegradationIncident degradation ->
                switch (degradation.getPerformanceLossPercent()) {
                    case int loss when loss >= 70 -> "SEVERE DEGRADATION";
                    case int loss when loss >= 30 -> "MODERATE DEGRADATION";
                    case int loss -> "MINOR DEGRADATION";
                };
        };
    }
}
