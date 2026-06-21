package org.urban.alert.exception.problem;

public class NotAgentException extends RuntimeException {
    public NotAgentException(Long userId) {
        super("L'utilisateur avec l'ID " + userId + " n'est pas un Agent");
    }

    public NotAgentException(String message) {
        super(message);
    }
}
