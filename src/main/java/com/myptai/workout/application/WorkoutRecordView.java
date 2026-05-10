package com.myptai.workout.application;

import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutRecord;
import com.myptai.workout.domain.WorkoutType;
import java.time.LocalDate;

public record WorkoutRecordView(
        Long id,
        LocalDate recordedOn,
        WorkoutType workoutType,
        String title,
        Integer durationMinutes,
        WorkoutIntensity intensity,
        String memo
) {

    public static WorkoutRecordView from(WorkoutRecord workoutRecord) {
        return new WorkoutRecordView(
                workoutRecord.getId(),
                workoutRecord.getRecordedOn(),
                workoutRecord.getWorkoutType(),
                workoutRecord.getTitle(),
                workoutRecord.getDurationMinutes(),
                workoutRecord.getIntensity(),
                workoutRecord.getMemo()
        );
    }
}
