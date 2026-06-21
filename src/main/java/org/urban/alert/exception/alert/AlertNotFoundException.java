package org.urban.alert.exception.alert;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(Long id) {
        super("L'alerte avec l'ID " + id + " n'a pas été trouvée");
    }

    public AlertNotFoundException(String message) {
        super(message);
    }
}

class AlertCannotBeModifiedException extends RuntimeException {
    public AlertCannotBeModifiedException(Long id) {
        super("L'alerte avec l'ID " + id + " ne peut pas être modifiée. Son statut n'est pas 'NEW'");
    }

    public AlertCannotBeModifiedException(String message) {
        super(message);
    }
}

class AlertCannotBeDeletedException extends RuntimeException {
    public AlertCannotBeDeletedException(Long id) {
        super("L'alerte avec l'ID " + id + " ne peut pas être supprimée. Son statut n'est pas 'NEW'");
    }

    public AlertCannotBeDeletedException(String message) {
        super(message);
    }
}
