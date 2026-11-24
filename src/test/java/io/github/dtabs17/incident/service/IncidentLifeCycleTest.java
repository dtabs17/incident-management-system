package io.github.dtabs17.incident.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.dtabs17.incident.domain.Engineer;
import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.domain.OutageIncident;
import io.github.dtabs17.incident.domain.Severity;
import io.github.dtabs17.incident.exception.InvalidIncidentStateException;

class IncidentLifecycleTest {

    private static final Instant FIXED_TIME =
            Instant.parse("2026-11-24T12:00:00Z");

    private IncidentManager manager;
    private Engineer engineer;
    private Incident incident;

    @BeforeEach
    void setUp() {
        manager = new IncidentManager(
                Clock.fixed(FIXED_TIME, ZoneOffset.UTC));

        engineer = new Engineer("ENG-001", "Alex Morgan");

        incident = manager.registerOutage(
                "Payment API unavailable", Severity.CRITICAL, 120);
    }

    @Test
    void engineerRecordValidatesAndNormalisesDetails() {
        Engineer trimmed = new Engineer(" ENG-002 ", " Jamie Lee ");

        assertEquals("ENG-002", trimmed.id());
        assertEquals("Jamie Lee", trimmed.name());

        assertThrows(IllegalArgumentException.class,
                () -> new Engineer("", "Jamie"));

        assertThrows(IllegalArgumentException.class,
                () -> new Engineer("ENG-003", " "));
    }

    @Test
    void assignmentAndReassignmentAreAllowedBeforeWorkStarts() {
        manager.assignEngineer(incident.getId(), engineer);

        assertEquals(IncidentStatus.ASSIGNED, incident.getStatus());
        assertEquals(engineer, incident.getAssignee().orElseThrow());

        Engineer replacement = new Engineer("ENG-002", "Jamie Lee");

        manager.assignEngineer(incident.getId(), replacement);

        assertEquals(replacement, incident.getAssignee().orElseThrow());
        assertEquals(IncidentStatus.ASSIGNED, incident.getStatus());
    }

    @Test
    void incidentProgressesToResolvedWithTimestamp() {
        manager.assignEngineer(incident.getId(), engineer);
        manager.startProgress(incident.getId());

        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());

        manager.resolveIncident(incident.getId());

        assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
        assertEquals(FIXED_TIME, incident.getResolvedAt().orElseThrow());
    }

    @Test
    void workCannotStartBeforeAssignment() {
        assertThrows(InvalidIncidentStateException.class,
                () -> manager.startProgress(incident.getId()));

        assertEquals(IncidentStatus.OPEN, incident.getStatus());
    }

    @Test
    void incidentCannotResolveBeforeWorkStarts() {
        assertThrows(InvalidIncidentStateException.class,
                () -> manager.resolveIncident(incident.getId()));

        manager.assignEngineer(incident.getId(), engineer);

        assertThrows(InvalidIncidentStateException.class,
                () -> manager.resolveIncident(incident.getId()));

        assertTrue(incident.getResolvedAt().isEmpty());
    }

    @Test
    void cannotReassignIncidentAfterWorkStarts() {
        manager.assignEngineer(incident.getId(), engineer);
        manager.startProgress(incident.getId());

        assertThrows(InvalidIncidentStateException.class,
                () -> manager.assignEngineer(
                        incident.getId(),
                        new Engineer("ENG-002", "Jamie Lee")));

        assertEquals(engineer, incident.getAssignee().orElseThrow());
    }

    @Test
    void resolvedIncidentCannotBeRestartedOrResolvedTwice() {
        manager.assignEngineer(incident.getId(), engineer);
        manager.startProgress(incident.getId());
        manager.resolveIncident(incident.getId());

        assertThrows(InvalidIncidentStateException.class,
                () -> manager.startProgress(incident.getId()));

        assertThrows(InvalidIncidentStateException.class,
                () -> manager.resolveIncident(incident.getId()));

        assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
    }

    @Test
    void missingIdIsReportedClearly() {
        assertThrows(NoSuchElementException.class,
                () -> manager.assignEngineer("INC-999", engineer));

        assertThrows(IllegalArgumentException.class,
                () -> manager.startProgress(" "));
    }

    @Test
    void nullEngineerIsRejectedWithoutChangingTheIncident() {
        assertThrows(NullPointerException.class,
                () -> manager.assignEngineer(incident.getId(), null));

        assertEquals(IncidentStatus.OPEN, incident.getStatus());
        assertTrue(incident.getAssignee().isEmpty());
    }

    @Test
    void earlierResolutionTimeIsRejectedWithoutChangingState() {
        OutageIncident future = new OutageIncident(
                "INC-900",
                "Future dated",
                Severity.LOW,
                FIXED_TIME.plusSeconds(60),
                10);

        future.assignTo(engineer);
        future.startProgress();

        assertThrows(IllegalArgumentException.class,
                () -> future.resolve(FIXED_TIME));

        assertEquals(IncidentStatus.IN_PROGRESS, future.getStatus());
        assertTrue(future.getResolvedAt().isEmpty());
    }
}
