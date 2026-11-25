package io.github.dtabs17.incident.report;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IncidentReportWriterTest {

    private final IncidentReportWriter writer =
            new IncidentReportWriter();

    private IncidentReport sampleReport() {
        return new IncidentReport(
                Instant.parse("2026-11-25T12:00:00Z"),
                new String[]{"INC-001 | Payment outage | OPEN"}
        );
    }

    @Test
    void writesReportToTextFile(@TempDir Path directory)
            throws IOException {

        Path destination = directory.resolve("report.txt");
        IncidentReport report = sampleReport();

        writer.write(report, destination);

        assertTrue(Files.exists(destination));
        assertEquals(
                report.toText(),
                Files.readString(destination, StandardCharsets.UTF_8)
        );
    }

    @Test
    void propagatesFileWritingFailures(@TempDir Path directory) {
        Path destination = directory.resolve("missing")
                .resolve("report.txt");

        assertThrows(IOException.class,
                () -> writer.write(sampleReport(), destination));
    }

    @Test
    void rejectsNullArguments(@TempDir Path directory) {
        assertThrows(NullPointerException.class,
                () -> writer.write(null, directory.resolve("report.txt")));

        assertThrows(NullPointerException.class,
                () -> writer.write(sampleReport(), null));
    }
}
