package io.github.dtabs17.incident.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class IncidentReportWriter {

    public void write(IncidentReport report, Path destination)
            throws IOException {

        Objects.requireNonNull(report, "Report is required");
        Objects.requireNonNull(destination, "Destination is required");

        Files.writeString(
                destination,
                report.toText(),
                StandardCharsets.UTF_8
        );
    }
}
