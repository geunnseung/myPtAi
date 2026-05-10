package com.myptai.workout.domain;

import java.time.LocalDate;

public record WorkoutRecordCommandValues(
        LocalDate recordedOn,
        WorkoutType workoutType,
        String title,
        Integer durationMinutes,
        WorkoutIntensity intensity,
        String memo
) {
}
