package io.github.dtabs17.incident.cli;

import java.io.PrintStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import io.github.dtabs17.incident.domain.IncidentImpactClassifier;
import io.github.dtabs17.incident.domain.Severity;
import io.github.dtabs17.incident.domain.SlaTracked;
import io.github.dtabs17.incident.service.IncidentManager;

public final class IncidentCli {

    private final IncidentManager manager;
    private final Scanner input;
    private final PrintStream out;

    public IncidentCli(IncidentManager manager, Scanner input,
            PrintStream out) {
        this.manager = Objects.requireNonNull(manager);
        this.input = Objects.requireNonNull(input);
        this.out = Objects.requireNonNull(out);
    }

    public void run() {
        while (true) {
            showMenu();

            if (!input.hasNextLine()) {
                return;
            }

            String choice = input.nextLine().strip();

            try {
                switch (choice) {
                    case "1" -> registerOutage();
                    case "2" -> registerDegradation();
                    case "3" -> listIncidents();
                    case "0" -> {
                        out.println("Goodbye!");
                        return;
                    }
                    default -> out.println("Invalid menu option.");
                }
            } catch (IllegalArgumentException e) {
                out.println("Error: " + e.getMessage());
            } catch (NoSuchElementException e) {
                out.println("Input ended.");
                return;
            }
        }
    }

    private void showMenu() {
        out.println();
        out.println("====== INCIDENT MANAGEMENT ======");
        out.println("1. Register outage");
        out.println("2. Register degradation");
        out.println("3. View all incidents");
        out.println("0. Exit");
        out.print("Choose an option: ");
    }

    private void registerOutage() {
        String title = readText("Incident title: ");
        Severity severity = readSeverity();
        int users = readNumber("Affected user count: ");

        var incident = manager.registerOutage(title, severity, users);
        out.println("Registered incident: " + incident.getId());
    }

    private void registerDegradation() {
        String title = readText("Incident title: ");
        Severity severity = readSeverity();
        int loss = readNumber("Performance loss (%): ");

        var incident = manager.registerDegradation(title, severity, loss);
        out.println("Registered incident: " + incident.getId());
    }

    private void listIncidents() {
        var incidents = manager.getAllIncidents();

        if (incidents.isEmpty()) {
            out.println("No incidents registered.");
            return;
        }

        for (var incident : incidents) {
            out.println();
            out.println(incident.summary());
            out.println("SLA deadline: "
                    + SlaTracked.formatDeadline(incident.getDeadline()));
            out.println("Impact category: "
                    + IncidentImpactClassifier.classify(incident));
        }
    }

    private String readText(String prompt) {
        out.print(prompt);
        return input.nextLine().strip();
    }

    private Severity readSeverity() {
        String value = readText(
                "Severity (LOW/MEDIUM/HIGH/CRITICAL): ");

        return Severity.valueOf(value.toUpperCase(Locale.ROOT));
    }

    private int readNumber(String prompt) {
        try {
            return Integer.parseInt(readText(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Please enter a valid whole number.");
        }
    }
}
