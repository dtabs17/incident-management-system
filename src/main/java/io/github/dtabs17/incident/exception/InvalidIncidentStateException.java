package io.github.dtabs17.incident.exception;

public class InvalidIncidentStateException extends IllegalStateException {

    public InvalidIncidentStateException() {
        super();
    }

    public InvalidIncidentStateException(String message) {
        super(message);
    }

    public InvalidIncidentStateException(Exception cause) {
        super(cause);
    }
}
