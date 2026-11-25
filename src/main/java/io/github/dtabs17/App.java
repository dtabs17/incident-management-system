package io.github.dtabs17;

import io.github.dtabs17.incident.domain.Engineer;
import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.Severity;
import io.github.dtabs17.incident.service.IncidentManager;
import io.github.dtabs17.incident.domain.SlaTracked;

import io.github.dtabs17.incident.domain.IncidentStatus;
import io.github.dtabs17.incident.service.IncidentFilters;

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

                manager.getAllIncidents().forEach(incident -> {
                        System.out.println(incident.summary());
                        System.out.println("SLA deadline: "
                                        + SlaTracked.formatDeadline(incident.getDeadline()));
                });

                Severity[] priorityLevels = {
                                Severity.HIGH,
                                Severity.CRITICAL
                };

                var filter = IncidentFilters.bySeverities(priorityLevels)
                                .and(IncidentFilters.byStatus(IncidentStatus.OPEN));

                var report = new StringBuilder(
                                "Open high-priority incidents:" + System.lineSeparator());

                manager.findIncidents(filter)
                                .stream()
                                .map(Incident::summary)
                                .forEach(summary -> report.append(summary).append(System.lineSeparator()));

                System.out.print(report);

        }
}
