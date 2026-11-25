package io.github.dtabs17.incident.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class IncidentImpactClassifierTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-11-25T12:00:00Z");

    private OutageIncident outage(int users) {
        return new OutageIncident(
                "INC-100", "Service outage",
                Severity.HIGH, CREATED_AT, users);
    }

    private DegradationIncident degradation(int loss) {
        return new DegradationIncident(
                "INC-200", "Slow service",
                Severity.HIGH, CREATED_AT, loss);
    }

    @Test
    void classifiesOutagesAtBoundaryValues() {
        assertEquals("LOCAL OUTAGE",
                IncidentImpactClassifier.classify(outage(99)));

        assertEquals("SIGNIFICANT OUTAGE",
                IncidentImpactClassifier.classify(outage(100)));

        assertEquals("SIGNIFICANT OUTAGE",
                IncidentImpactClassifier.classify(outage(499)));

        assertEquals("WIDESPREAD OUTAGE",
                IncidentImpactClassifier.classify(outage(500)));
    }

    @Test
    void classifiesDegradationsAtBoundaryValues() {
        assertEquals("MINOR DEGRADATION",
                IncidentImpactClassifier.classify(degradation(29)));

        assertEquals("MODERATE DEGRADATION",
                IncidentImpactClassifier.classify(degradation(30)));

        assertEquals("MODERATE DEGRADATION",
                IncidentImpactClassifier.classify(degradation(69)));

        assertEquals("SEVERE DEGRADATION",
                IncidentImpactClassifier.classify(degradation(70)));
    }

    @Test
    void rejectsNullIncidents() {
        assertThrows(NullPointerException.class,
                () -> IncidentImpactClassifier.classify(null));
    }
}
