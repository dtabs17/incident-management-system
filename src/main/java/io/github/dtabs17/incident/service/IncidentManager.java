package io.github.dtabs17.incident.service;

import java.util.NoSuchElementException;
import io.github.dtabs17.incident.domain.Engineer;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import io.github.dtabs17.incident.domain.DegradationIncident;
import io.github.dtabs17.incident.domain.Incident;
import io.github.dtabs17.incident.domain.OutageIncident;
import io.github.dtabs17.incident.domain.Severity;

public final class IncidentManager {

    private final List<Incident> incidents = new ArrayList<>();
    private final Clock clock;
    private int nextIncidentNumber = 1;

    public IncidentManager() {
        this(Clock.systemUTC());
    }

    public IncidentManager(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    public List<Incident> findIncidents(Predicate<Incident> filter) {
        Objects.requireNonNull(filter, "Filter is required");

        return incidents.stream()
                .filter(filter)
                .toList();
    }

    public OutageIncident registerOutage(
            String title, Severity severity, int affectedUserCount) {

        OutageIncident incident = new OutageIncident(
                generateNextId(),
                title,
                severity,
                Instant.now(clock),
                affectedUserCount);

        incidents.add(incident);
        nextIncidentNumber++;

        return incident;
    }

    public DegradationIncident registerDegradation(
            String title, Severity severity, int performanceLossPercent) {

        DegradationIncident incident = new DegradationIncident(
                generateNextId(),
                title,
                severity,
                Instant.now(clock),
                performanceLossPercent);

        incidents.add(incident);
        nextIncidentNumber++;

        return incident;
    }

    public List<Incident> getAllIncidents() {
        return List.copyOf(incidents);
    }

    public Optional<Incident> findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Incident ID is required");
        }

        String searchId = id.strip();

        return incidents.stream()
                .filter(incident -> incident.getId().equals(searchId))
                .findFirst();
    }

    public Incident assignEngineer(String id, Engineer engineer) {
        Incident incident = requireIncident(id);
        incident.assignTo(engineer);
        return incident;
    }

    public Incident startProgress(String id) {
        Incident incident = requireIncident(id);
        incident.startProgress();
        return incident;
    }

    public Incident resolveIncident(String id) {
        Incident incident = requireIncident(id);
        incident.resolve(Instant.now(clock));
        return incident;
    }

    private Incident requireIncident(String id) {
        return findById(id).orElseThrow(
                () -> new NoSuchElementException(
                        "Incident not found: " + id.strip()));
    }

    private String generateNextId() {
        return "INC-%03d".formatted(nextIncidentNumber);
    }

}