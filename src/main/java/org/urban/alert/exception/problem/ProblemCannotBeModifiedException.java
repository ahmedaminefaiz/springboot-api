package org.urban.alert.exception.problem;

public class ProblemCannotBeModifiedException extends RuntimeException {
    public ProblemCannotBeModifiedException(Long id) {
        super("Le problème avec l'ID " + id + " ne peut pas être modifié car il est " + 
              "RESOLVED ou REJECTED");
    }

    public ProblemCannotBeModifiedException(String message) {
        super(message);
    }
}
