package org.urban.alert.exception.problem;

public class AlertAlreadyAssignedException extends RuntimeException {
    public AlertAlreadyAssignedException(Long alertId) {
        super("L'alerte avec l'ID " + alertId + " est déjà assignée à un problème");
    }

    public AlertAlreadyAssignedException(String message) {
        super(message);
    }
}
