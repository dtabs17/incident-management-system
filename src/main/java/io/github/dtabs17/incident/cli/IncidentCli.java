package io.github.dtabs17.incident.cli;

import java.io.PrintStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

import io.github.dtabs17.incident.domain.Engineer;
import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.report.IncidentReportWriter;
import io.github.dtabs17.incident.service.IncidentFilters;

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
                    case "4" -> assignEngineer();
                    case "5" -> startProgress();
                    case "6" -> resolveIncident();
                    case "7" -> showFilteredReport();
                    case "8" -> exportReport();
                    case "0" -> {
                        out.println("Goodbye!");
                        return;
                    }
                    default -> out.println("Invalid menu option.");
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | NoSuchElementException e) {
                out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showMenu() {
        out.println();
        out.println("====== INCIDENT MANAGEMENT ======");
        out.println("1. Register outage");
        out.println("2. Register degradation");
        out.println("3. View all incidents");
        out.println("4. Assign engineer");
        out.println("5. Start investigation");
        out.println("6. Resolve incident");
        out.println("7. Generate filtered report");
        out.println("8. Export report to file");
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

    private void assignEngineer() {
        String incidentId = readText("Incident ID: ");
        String engineerId = readText("Engineer ID: ");
        String engineerName = readText("Engineer name: ");

        var engineer = new Engineer(engineerId, engineerName);
        var incident = manager.assignEngineer(incidentId, engineer);

        out.println("Engineer assigned to " + incident.getId());
    }

    private void startProgress() {
        String id = readText("Incident ID: ");

        var incident = manager.startProgress(id);

        out.println("Investigation started for " + incident.getId());
    }

    private void resolveIncident() {
        String id = readText("Incident ID: ");

        var incident = manager.resolveIncident(id);

        out.println("Incident resolved: " + incident.getId());
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

    private Predicate<Incident> readReportFilter() {
        String status = readText(
                "Status (ALL/OPEN/ASSIGNED/IN_PROGRESS/RESOLVED): ")
                .toUpperCase(Locale.ROOT);

        Predicate<Incident> filter = incident -> true;

        if (!status.equals("ALL")) {
            filter = filter.and(
                    IncidentFilters.byStatus(IncidentStatus.valueOf(status)));
        }

        String severities = readText(
                "Severities (ALL or comma-separated, e.g. HIGH,CRITICAL): ")
                .toUpperCase(Locale.ROOT);

        if (!severities.equals("ALL")) {
            String[] values = severities.split(",", -1);
            Severity[] selected = new Severity[values.length];

            for (int i = 0; i < values.length; i++) {
                selected[i] = Severity.valueOf(values[i].strip());
            }

            filter = filter.and(IncidentFilters.bySeverities(selected));
        }

        return filter;
    }

    private void showFilteredReport() {
        var report = manager.generateReport(readReportFilter());

        if (report.lines().length == 0) {
            out.println("No incidents match the selected filters.");
            return;
        }

        out.print(report.toText());
    }

    private void exportReport() {
        var report = manager.generateReport(readReportFilter());

        if (report.lines().length == 0) {
            out.println("No incidents match the selected filters.");
            return;
        }

        Path destination = Path.of(readText("File path: "));

        try {
            new IncidentReportWriter().write(report, destination);
            out.println("Report saved to: " + destination.toAbsolutePath());
        } catch (IOException e) {
            out.println("Could not save report: " + e.getMessage());
        }
    }

}
