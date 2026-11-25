package io.github.dtabs17.incident.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import io.github.dtabs17.incident.domain.OutageIncident;
import io.github.dtabs17.incident.service.IncidentManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;

import io.github.dtabs17.incident.domain.Engineer;
import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.domain.Severity;

class IncidentCliTest {

    private String runMenu(IncidentManager manager, String commands) {
        var buffer = new ByteArrayOutputStream();

        try (var input = new Scanner(commands);
                var output = new PrintStream(
                        buffer, true, StandardCharsets.UTF_8)) {

            new IncidentCli(manager, input, output).run();
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void registersAndDisplaysAnOutage() {
        var manager = new IncidentManager();

        String output = runMenu(manager, """
                1
                Payment API down
                CRITICAL
                120
                3
                0
                """);

        assertEquals(1, manager.getAllIncidents().size());
        assertInstanceOf(OutageIncident.class,
                manager.getAllIncidents().getFirst());

        assertTrue(output.contains("Registered incident: INC-001"));
        assertTrue(output.contains("Payment API down"));
        assertTrue(output.contains("SIGNIFICANT OUTAGE"));
    }

    @Test
    void rejectsInvalidIncidentAndContinues() {
        var manager = new IncidentManager();

        String output = runMenu(manager, """
                1
                Invalid outage
                HIGH
                0
                2
                Slow search
                MEDIUM
                25
                0
                """);

        assertTrue(output.contains(
                "Affected user count must be positive"));
        assertTrue(output.contains("Registered incident: INC-001"));
        assertEquals(1, manager.getAllIncidents().size());
    }

    @Test
    void handlesInvalidMenuChoicesAndEmptyLists() {
        var manager = new IncidentManager();

        String output = runMenu(manager, """
                9
                3
                0
                """);

        assertTrue(output.contains("Invalid menu option."));
        assertTrue(output.contains("No incidents registered."));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void completesFullIncidentLifecycle() {
        var manager = new IncidentManager();

        String output = runMenu(manager, """
                1
                Payment outage
                CRITICAL
                120
                4
                INC-001
                ENG-001
                Alex Morgan
                5
                INC-001
                6
                INC-001
                3
                0
                """);

        var incident = manager.findById("INC-001").orElseThrow();

        assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
        assertTrue(output.contains("Engineer assigned"));
        assertTrue(output.contains("Investigation started"));
        assertTrue(output.contains("Incident resolved"));
    }

    @Test
    void invalidStatusTransitionDoesNotCrashMenu() {
        var manager = new IncidentManager();
        manager.registerOutage("Payment outage", Severity.HIGH, 120);

        String output = runMenu(manager, """
                5
                INC-001
                0
                """);

        assertTrue(output.contains(
                "Only assigned incidents can start progress"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void filtersReportsByStatusAndSeverity() {
        var manager = new IncidentManager();

        var outage = manager.registerOutage(
                "Payment outage", Severity.HIGH, 120);

        manager.assignEngineer(
                outage.getId(), new Engineer("ENG-001", "Alex Morgan"));

        manager.registerDegradation(
                "Search slowdown", Severity.HIGH, 45);

        String output = runMenu(manager, """
                7
                OPEN
                HIGH
                0
                """);

        assertTrue(output.contains("Search slowdown"));
        assertFalse(output.contains("Payment outage"));
    }

    @Test
    void exportsReportToFile(@TempDir Path directory)
            throws IOException {

        var manager = new IncidentManager();
        manager.registerOutage("Payment outage", Severity.CRITICAL, 120);

        Path destination = directory.resolve("report.txt");

        String output = runMenu(manager,
                "8\nALL\nHIGH,CRITICAL\n" + destination + "\n0\n");

        assertTrue(Files.exists(destination));
        assertTrue(Files.readString(destination).contains("Payment outage"));
        assertTrue(output.contains("Report saved to:"));
    }

    @Test
    void handlesFileExportFailure(@TempDir Path directory) {
        var manager = new IncidentManager();
        manager.registerOutage("Payment outage", Severity.HIGH, 120);

        Path destination = directory.resolve("missing")
                .resolve("report.txt");

        String output = runMenu(manager,
                "8\nALL\nALL\n" + destination + "\n0\n");

        assertTrue(output.contains("Could not save report:"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void missingIncidentDoesNotCrashMenu() {
        var manager = new IncidentManager();

        String output = runMenu(manager, """
                5
                INC-999
                3
                0
                """);

        assertTrue(output.contains("Incident not found: INC-999"));
        assertTrue(output.contains("No incidents registered."));
        assertTrue(output.contains("Goodbye!"));
    }

}
