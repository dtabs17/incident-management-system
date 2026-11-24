package io.github.dtabs17;

import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.Severity;
import io.github.dtabs17.incident.service.IncidentManager;

public class App {

    public static void main(String[] args) {

        IncidentManager manager = new IncidentManager();

        manager.registerOutage(
                "Payment API unavailable",
                Severity.CRITICAL,
                120
        );

        manager.registerDegradation(
                "Search is slow",
                Severity.HIGH,
                45
        );

        manager.getAllIncidents()
                .stream()
                .map(Incident::summary)
                .forEach(System.out::println);
    }
}
