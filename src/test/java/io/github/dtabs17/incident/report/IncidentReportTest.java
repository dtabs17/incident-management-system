package io.github.dtabs17.incident.report;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class IncidentReportTest {

    private static final Instant REPORT_TIME =
            Instant.parse("2026-11-25T12:00:00Z");

    @Test
    void constructorCopiesTheOriginalArray() {
        String[] original = {"INC-001 | OPEN"};

        IncidentReport report = new IncidentReport(
                REPORT_TIME, original);

        original[0] = "CHANGED";

        assertEquals("INC-001 | OPEN", report.lines()[0]);
    }

    @Test
    void returnedArrayCannotModifyReport() {
        IncidentReport report = new IncidentReport(
                REPORT_TIME,
                new String[]{"INC-001 | OPEN"});

        String[] returned = report.lines();
        returned[0] = "CHANGED";

        assertEquals("INC-001 | OPEN", report.lines()[0]);
    }

    @Test
    void reportContainsTimestampAndIncidentDetails() {
        IncidentReport report = new IncidentReport(
                REPORT_TIME,
                new String[]{
                        "INC-001 | OPEN",
                        "INC-002 | RESOLVED"
                });

        String text = report.toText();

        assertTrue(text.contains(REPORT_TIME.toString()));
        assertTrue(text.contains("INC-001 | OPEN"));
        assertTrue(text.contains("INC-002 | RESOLVED"));
    }

    @Test
    void invalidReportDataIsRejected() {
        assertThrows(NullPointerException.class,
                () -> new IncidentReport(null, new String[0]));

        assertThrows(NullPointerException.class,
                () -> new IncidentReport(REPORT_TIME, null));

        assertThrows(IllegalArgumentException.class,
                () -> new IncidentReport(
                        REPORT_TIME, new String[]{" "}));
    }
}
