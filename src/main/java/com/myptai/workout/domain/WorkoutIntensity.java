package com.myptai.workout.domain;

public enum WorkoutIntensity {
    EASY("가벼움"),
    NORMAL("보통"),
    HARD("힘듦"),
    VERY_HARD("매우 힘듦");

    private final String label;

    WorkoutIntensity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
