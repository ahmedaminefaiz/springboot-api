package org.urban.alert.exception.intervention;

public class InterventionNotFoundException extends RuntimeException {
    public InterventionNotFoundException(Long id) {
        super("Intervention avec l'ID " + id + " n'a pas été trouvée");
    }

    public InterventionNotFoundException(String message) {
        super(message);
    }
}
