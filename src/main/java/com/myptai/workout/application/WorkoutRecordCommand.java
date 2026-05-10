package com.myptai.workout.application;

import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutRecordCommandValues;
import com.myptai.workout.domain.WorkoutType;
import java.time.LocalDate;

public record WorkoutRecordCommand(
        LocalDate recordedOn,
        WorkoutType workoutType,
        String title,
        Integer durationMinutes,
        WorkoutIntensity intensity,
        String memo
) {

    public WorkoutRecordCommandValues toValues() {
        return new WorkoutRecordCommandValues(recordedOn, workoutType, title, durationMinutes, intensity, memo);
    }
}
