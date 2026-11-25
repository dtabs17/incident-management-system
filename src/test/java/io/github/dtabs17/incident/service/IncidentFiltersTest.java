package io.github.dtabs17.incident.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import io.github.dtabs17.incident.domain.DegradationIncident;
import io.github.dtabs17.incident.domain.Engineer;
import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.domain.OutageIncident;
import io.github.dtabs17.incident.domain.Severity;

class IncidentFiltersTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-11-24T12:00:00Z");

    private OutageIncident outage() {
        return new OutageIncident(
                "INC-001", "Payment outage",
                Severity.CRITICAL, CREATED_AT, 100);
    }

    private DegradationIncident degradation() {
        return new DegradationIncident(
                "INC-002", "Slow API",
                Severity.HIGH, CREATED_AT, 40);
    }

    @Test
    void filterAcceptsMultipleSeverities() {
        var filter = IncidentFilters.bySeverities(
                Severity.CRITICAL, Severity.HIGH);

        assertTrue(filter.test(outage()));
        assertTrue(filter.test(degradation()));

        var lowIncident = new OutageIncident(
                "INC-003", "Minor outage",
                Severity.LOW, CREATED_AT, 5);

        assertFalse(filter.test(lowIncident));
    }

    @Test
    void changingOriginalArrayDoesNotChangeFilter() {
        Severity[] selected = {Severity.CRITICAL};

        var filter = IncidentFilters.bySeverities(selected);

        selected[0] = Severity.LOW;

        assertTrue(filter.test(outage()));
        assertFalse(filter.test(degradation()));
    }

    @Test
    void filterMatchesIncidentStatus() {
        var incident = outage();

        assertTrue(IncidentFilters.byStatus(
                IncidentStatus.OPEN).test(incident));

        incident.assignTo(new Engineer("ENG-001", "Alex Morgan"));

        assertTrue(IncidentFilters.byStatus(
                IncidentStatus.ASSIGNED).test(incident));

        assertFalse(IncidentFilters.byStatus(
                IncidentStatus.OPEN).test(incident));
    }

    @Test
    void filterMatchesConcreteIncidentType() {
        var filter = IncidentFilters.byType(OutageIncident.class);

        assertTrue(filter.test(outage()));
        assertFalse(filter.test(degradation()));
    }

    @Test
    void predicatesCanBeCombined() {
        Predicate<Incident> filter =
                IncidentFilters.bySeverities(Severity.HIGH)
                        .and(IncidentFilters.byType(
                                DegradationIncident.class));

        assertTrue(filter.test(degradation()));
        assertFalse(filter.test(outage()));
    }

    @Test
    void invalidFilterArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> IncidentFilters.bySeverities());

        assertThrows(NullPointerException.class,
                () -> IncidentFilters.bySeverities(
                        (Severity[]) null));

        assertThrows(NullPointerException.class,
                () -> IncidentFilters.bySeverities(
                        Severity.HIGH, null));

        assertThrows(NullPointerException.class,
                () -> IncidentFilters.byStatus(null));

        assertThrows(NullPointerException.class,
                () -> IncidentFilters.byType(null));
    }
}
