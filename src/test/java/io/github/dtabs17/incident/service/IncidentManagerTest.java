package io.github.dtabs17.incident.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.domain.Severity;

class IncidentManagerTest {

    private IncidentManager manager;

    private static final Instant FIXED_TIME =
            Instant.parse("2026-11-24T12:00:00Z");

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
        manager = new IncidentManager(clock);
    }

    @Test
    void registeringIncidentsGeneratesUniqueIds() {
        Incident first = manager.registerOutage(
                "Payment API unavailable", Severity.CRITICAL, 120);

        Incident second = manager.registerDegradation(
                "Search is slow", Severity.HIGH, 45);

        assertEquals("INC-001", first.getId());
        assertEquals("INC-002", second.getId());
        assertEquals(2, manager.getAllIncidents().size());
    }

    @Test
    void newIncidentsUseTheProvidedClockAndStartOpen() {
        Incident incident = manager.registerOutage(
                "Database unavailable", Severity.HIGH, 80);

        assertEquals(FIXED_TIME, incident.getCreatedAt());
        assertEquals(IncidentStatus.OPEN, incident.getStatus());
    }

    @Test
    void invalidRegistrationDoesNotConsumeAnId() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.registerOutage(
                        "Invalid outage", Severity.HIGH, 0));

        Incident valid = manager.registerOutage(
                "Valid outage", Severity.HIGH, 10);

        assertEquals("INC-001", valid.getId());
        assertEquals(1, manager.getAllIncidents().size());
    }

    @Test
    void returnedIncidentListCannotBeModified() {
        manager.registerOutage(
                "Payment outage", Severity.CRITICAL, 100);

        List<Incident> snapshot = manager.getAllIncidents();

        assertThrows(UnsupportedOperationException.class,
                snapshot::clear);

        manager.registerDegradation(
                "Search degradation", Severity.MEDIUM, 30);

        assertEquals(1, snapshot.size());
        assertEquals(2, manager.getAllIncidents().size());
    }

    @Test
    void incidentsCanBeFoundById() {
        Incident incident = manager.registerOutage(
                "Network outage", Severity.HIGH, 50);

        assertSame(incident,
                manager.findById("INC-001").orElseThrow());

        assertTrue(manager.findById("INC-999").isEmpty());
    }
}
