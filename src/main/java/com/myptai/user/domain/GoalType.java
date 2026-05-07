package com.myptai.user.domain;

public enum GoalType {
    FAT_LOSS("체지방 감량"),
    MUSCLE_GAIN("근육 증가"),
    MAINTENANCE("체중 유지"),
    HEALTH("건강 관리");

    private final String label;

    GoalType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
