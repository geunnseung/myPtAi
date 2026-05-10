package com.myptai.workout.domain;

public enum WorkoutType {
    STRENGTH("웨이트"),
    CARDIO("유산소"),
    MOBILITY("스트레칭"),
    SPORTS("스포츠"),
    REST("휴식");

    private final String label;

    WorkoutType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
