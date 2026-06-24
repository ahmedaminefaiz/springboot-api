package org.urban.alert.exception.intervention;

public class InterventionCannotBeModifiedException extends RuntimeException {
    public InterventionCannotBeModifiedException(Long id) {
        super("L'intervention " + id + " est clôturée et ne peut pas être modifiée");
    }

    public InterventionCannotBeModifiedException(String message) {
        super(message);
    }
}
