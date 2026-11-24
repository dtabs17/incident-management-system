package io.github.dtabs17;

import java.util.List;

import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.OutageIncident;
import io.github.dtabs17.incident.domain.DegradationIncident;
import io.github.dtabs17.incident.domain.Severity;

public class App {

    public static void main(String[] args) {

        List<Incident> incidents = List.of(
            new OutageIncident(
                "INC-001", "Payment API unavailable",
                Severity.CRITICAL, 120
            ),
            new DegradationIncident(
                "INC-002", "Search is slow",
                Severity.HIGH, 45
            )
        );

        incidents.stream()
                .map(Incident::summary)
                .forEach(System.out::println);
    }
}
