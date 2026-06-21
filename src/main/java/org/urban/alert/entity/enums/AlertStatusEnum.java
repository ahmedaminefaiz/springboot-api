package org.urban.alert.entity.enums;

public enum AlertStatusEnum {
    NEW("nouvelle"),
    IN_PROGRESS("en cours de traitement"),
    RESOLVED("résolue"),
    REJECTED("rejetée");

    private final String displayName;

    AlertStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
