package org.urban.alert.exception.problem;

public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException(Long id) {
        super("Problème avec l'ID " + id + " n'a pas été trouvé");
    }

    public ProblemNotFoundException(String message) {
        super(message);
    }
}

