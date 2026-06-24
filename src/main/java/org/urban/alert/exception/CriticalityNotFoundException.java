package org.urban.alert.exception;

public class CriticalityNotFoundException extends RuntimeException {
    public CriticalityNotFoundException(Long id) {
        super("Criticité avec l'ID " + id + " n'a pas été trouvée");
    }
}