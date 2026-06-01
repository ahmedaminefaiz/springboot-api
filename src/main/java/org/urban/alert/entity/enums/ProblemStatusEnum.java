package org.urban.alert.entity.enums;

import lombok.Getter;

@Getter
public enum ProblemStatusEnum {
    NEW("NEW", "Nouveau"),
    IN_PROGRESS("IN_PROGRESS", "En cours"),
    RESOLVED("RESOLVED", "Résolu"),
    REJECTED("REJECTED", "Rejeté");

    private final String code;
    private final String label;

    ProblemStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
