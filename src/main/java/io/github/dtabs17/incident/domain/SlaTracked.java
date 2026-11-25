package io.github.dtabs17.incident.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public interface SlaTracked {

    Instant getCreatedAt();

    IncidentStatus getStatus();

    Duration getResolutionWindow();

    default Instant getDeadline() {
        return getCreatedAt().plus(validatedResolutionWindow());
    }

    default boolean isOverdue(Instant now) {
        Objects.requireNonNull(now, "Current time is required");

        return getStatus() != IncidentStatus.RESOLVED
                && now.isAfter(getDeadline());
    }

    default boolean isOverdue(Clock clock) {
        Objects.requireNonNull(clock, "Clock is required");
        return isOverdue(clock.instant());
    }

    static String formatDeadline(Instant deadline) {
        Objects.requireNonNull(deadline, "Deadline is required");

        return DateTimeFormatter
                .ofPattern("dd MMM uuuu HH:mm 'UTC'", Locale.UK)
                .withZone(ZoneOffset.UTC)
                .format(deadline);
    }

    private Duration validatedResolutionWindow() {
        Duration window = Objects.requireNonNull(
                getResolutionWindow(), "Resolution window is required");

        if (window.isZero() || window.isNegative()) {
            throw new IllegalStateException(
                    "Resolution window must be positive");
        }

        return window;
    }
}
