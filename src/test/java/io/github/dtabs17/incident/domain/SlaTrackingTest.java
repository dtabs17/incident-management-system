package io.github.dtabs17.incident.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class SlaTrackingTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-11-24T12:00:00Z");

    private OutageIncident outage(Severity severity) {
        return new OutageIncident(
                "INC-101", "Payment outage", severity, CREATED_AT, 100);
    }

    private DegradationIncident degradation(Severity severity) {
        return new DegradationIncident(
                "INC-102", "Slow API", severity, CREATED_AT, 40);
    }

    @Test
    void outageResolutionWindowsDependOnSeverity() {
        assertEquals(Duration.ofHours(1),
                outage(Severity.CRITICAL).getResolutionWindow());
        assertEquals(Duration.ofHours(2),
                outage(Severity.HIGH).getResolutionWindow());
        assertEquals(Duration.ofHours(4),
                outage(Severity.MEDIUM).getResolutionWindow());
        assertEquals(Duration.ofHours(8),
                outage(Severity.LOW).getResolutionWindow());
    }

    @Test
    void degradationResolutionWindowsDependOnSeverity() {
        assertEquals(Duration.ofHours(2),
                degradation(Severity.CRITICAL).getResolutionWindow());
        assertEquals(Duration.ofHours(4),
                degradation(Severity.HIGH).getResolutionWindow());
        assertEquals(Duration.ofHours(8),
                degradation(Severity.MEDIUM).getResolutionWindow());
        assertEquals(Duration.ofHours(24),
                degradation(Severity.LOW).getResolutionWindow());
    }

    @Test
    void deadlineIsCalculatedAndFormattedInUtc() {
        SlaTracked incident = outage(Severity.CRITICAL);

        assertEquals(
                Instant.parse("2026-11-24T13:00:00Z"),
                incident.getDeadline());

        assertEquals(
                "24 Nov 2026 13:00 UTC",
                SlaTracked.formatDeadline(incident.getDeadline()));
    }

    @Test
    void incidentIsOverdueOnlyAfterItsDeadline() {
        SlaTracked incident = outage(Severity.CRITICAL);

        assertFalse(incident.isOverdue(
                CREATED_AT.plus(Duration.ofMinutes(30))));

        assertFalse(incident.isOverdue(
                CREATED_AT.plus(Duration.ofHours(1))));

        assertTrue(incident.isOverdue(
                CREATED_AT.plus(Duration.ofHours(1))
                        .plusSeconds(1)));
    }

    @Test
    void clockOverloadCanDetermineOverdueStatus() {
        SlaTracked incident = degradation(Severity.HIGH);

        Clock clock = Clock.fixed(
                CREATED_AT.plus(Duration.ofHours(5)),
                ZoneOffset.UTC);

        assertTrue(incident.isOverdue(clock));
    }

    @Test
    void resolvedIncidentsAreNotReportedAsCurrentlyOverdue() {
        OutageIncident incident = outage(Severity.CRITICAL);
        incident.assignTo(new Engineer("ENG-001", "Alex Morgan"));
        incident.startProgress();
        incident.resolve(CREATED_AT.plus(Duration.ofMinutes(45)));

        assertFalse(incident.isOverdue(
                CREATED_AT.plus(Duration.ofHours(3))));
    }

    @Test
    void nullTimeInputsAreRejected() {
        SlaTracked incident = outage(Severity.HIGH);

        assertThrows(NullPointerException.class,
                () -> incident.isOverdue((Instant) null));

        assertThrows(NullPointerException.class,
                () -> incident.isOverdue((Clock) null));

        assertThrows(NullPointerException.class,
                () -> SlaTracked.formatDeadline(null));
    }

    @Test
    void interfaceReferencesUseTheCorrectIncidentImplementation() {
        SlaTracked incident = outage(Severity.HIGH);

        assertEquals(Duration.ofHours(2),
                incident.getResolutionWindow());

        incident = degradation(Severity.HIGH);

        assertEquals(Duration.ofHours(4),
                incident.getResolutionWindow());
    }
}
