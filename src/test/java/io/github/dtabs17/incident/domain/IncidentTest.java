package io.github.dtabs17.incident.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class IncidentTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-11-23T12:00:00Z");

    @Test
    void outageStartsOpenAndDescribesItsImpact() {
        Incident incident = new OutageIncident(
                "INC-001", "Payment API unavailable",
                Severity.CRITICAL, CREATED_AT, 120);

        assertEquals(IncidentStatus.OPEN, incident.getStatus());
        assertEquals("Service unavailable; affected users: 120",
                incident.describeImpact());
    }

    @Test
    void degradationUsesItsOwnImplementation() {
        Incident incident = new DegradationIncident(
                "INC-002", "Search is slow",
                Severity.HIGH, CREATED_AT, 45);

        assertEquals(
                "Service degraded; estimated performance loss: 45%",
                incident.describeImpact());
    }

    @Test
    void incidentSummaryIncludesSubclassDetails() {
        Incident incident = new OutageIncident(
                "INC-003", "Database unavailable",
                Severity.HIGH, CREATED_AT, 80);

        assertTrue(incident.summary().contains("80"));
        assertTrue(incident.summary().contains("OPEN"));
    }

    @Test
    void invalidIncidentDataIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new OutageIncident(
                        "INC-004", "", Severity.HIGH, CREATED_AT, 50));

        assertThrows(IllegalArgumentException.class,
                () -> new OutageIncident(
                        "INC-005", "API outage",
                        Severity.HIGH, CREATED_AT, 0));

        assertThrows(IllegalArgumentException.class,
                () -> new DegradationIncident(
                        "INC-006", "Slow service",
                        Severity.LOW, CREATED_AT, 100));
    }
}
