package com.myptai.coaching.application;

import com.myptai.condition.domain.DailyCondition;
import com.myptai.meal.domain.MealRecord;
import com.myptai.workout.domain.WorkoutRecord;
import java.time.LocalDate;
import java.util.List;

public record CoachingContext(
        LocalDate startDate,
        LocalDate endDate,
        List<MealRecord> meals,
        List<WorkoutRecord> workouts,
        List<DailyCondition> conditions
) {
}
