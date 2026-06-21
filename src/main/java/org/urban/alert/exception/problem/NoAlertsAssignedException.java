package org.urban.alert.exception.problem;

public class NoAlertsAssignedException extends RuntimeException {
    public NoAlertsAssignedException() {
        super("Au moins une alerte doit être assignée au problème");
    }

    public NoAlertsAssignedException(String message) {
        super(message);
    }
}
