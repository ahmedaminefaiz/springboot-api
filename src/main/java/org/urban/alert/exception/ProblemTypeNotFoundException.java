package org.urban.alert.exception;

public class ProblemTypeNotFoundException extends RuntimeException {
    public ProblemTypeNotFoundException(Long id) {
        super("Type de problème avec l'ID " + id + " n'a pas été trouvé");
    }
}
