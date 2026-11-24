package io.github.dtabs17;

import io.github.dtabs17.incident.domain.Engineer;
import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.Severity;
import io.github.dtabs17.incident.service.IncidentManager;

public class App {

    public static void main(String[] args) {
        IncidentManager manager = new IncidentManager();

        Incident outage = manager.registerOutage(
                "Payment API unavailable", Severity.CRITICAL, 120);

        manager.registerDegradation(
                "Search is slow", Severity.HIGH, 45);

        Engineer engineer = new Engineer("ENG-001", "Alex Morgan");

        manager.assignEngineer(outage.getId(), engineer);
        manager.startProgress(outage.getId());
        manager.resolveIncident(outage.getId());

        manager.getAllIncidents().stream()
                .map(Incident::summary)
                .forEach(System.out::println);
    }
}
