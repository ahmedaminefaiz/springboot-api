package org.urban.alert.exception.problem;

public class NotSuperAgentException extends RuntimeException {
    public NotSuperAgentException() {
        super("Accès refusé : Seuls les Super Agents peuvent créer des problèmes");
    }

    public NotSuperAgentException(String message) {
        super(message);
    }
}
