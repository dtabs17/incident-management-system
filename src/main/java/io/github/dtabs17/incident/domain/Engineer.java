package io.github.dtabs17.incident.domain;

public record Engineer(String id, String name) {

    public Engineer {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Engineer ID is required");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Engineer name is required");
        }

        id = id.strip();
        name = name.strip();
    }
}
