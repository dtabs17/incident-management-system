package io.github.dtabs17.incident.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import io.github.dtabs17.incident.domain.OutageIncident;
import io.github.dtabs17.incident.service.IncidentManager;

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
}
