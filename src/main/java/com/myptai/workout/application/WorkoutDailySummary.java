package com.myptai.workout.application;

import com.myptai.workout.domain.WorkoutIntensity;
import java.util.List;

public record WorkoutDailySummary(
        int recordCount,
        int totalDurationMinutes,
        long hardWorkoutCount
) {

    public static WorkoutDailySummary from(List<WorkoutRecordView> workouts) {
        int duration = workouts.stream()
                .map(WorkoutRecordView::durationMinutes)
                .mapToInt(value -> value == null ? 0 : value)
                .sum();

        long hardCount = workouts.stream()
                .map(WorkoutRecordView::intensity)
                .filter(intensity -> intensity == WorkoutIntensity.HARD || intensity == WorkoutIntensity.VERY_HARD)
                .count();

        return new WorkoutDailySummary(workouts.size(), duration, hardCount);
    }
}
