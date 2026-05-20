package com.myptai.coaching.application;

import java.time.LocalDate;

public record AiCoachingContextView(
        LocalDate startDate,
        LocalDate endDate,
        int mealCount,
        int workoutCount,
        int conditionCount
) {

    public static AiCoachingContextView from(CoachingContext context) {
        return new AiCoachingContextView(
                context.startDate(),
                context.endDate(),
                context.meals().size(),
                context.workouts().size(),
                context.conditions().size()
        );
    }
}
