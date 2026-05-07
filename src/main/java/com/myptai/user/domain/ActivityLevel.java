package com.myptai.user.domain;

public enum ActivityLevel {
    LOW("낮음"),
    MODERATE("보통"),
    HIGH("높음"),
    VERY_HIGH("매우 높음");

    private final String label;

    ActivityLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
