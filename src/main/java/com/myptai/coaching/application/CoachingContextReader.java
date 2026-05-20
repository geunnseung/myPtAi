package com.myptai.coaching.application;

import com.myptai.condition.domain.DailyCondition;
import com.myptai.condition.repository.DailyConditionRepository;
import com.myptai.meal.domain.MealRecord;
import com.myptai.meal.repository.MealRecordRepository;
import com.myptai.user.domain.AppUser;
import com.myptai.workout.domain.WorkoutRecord;
import com.myptai.workout.repository.WorkoutRecordRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CoachingContextReader {

    static final int CONTEXT_DAYS = 7;

    private final MealRecordRepository mealRecordRepository;
    private final WorkoutRecordRepository workoutRecordRepository;
    private final DailyConditionRepository dailyConditionRepository;

    public CoachingContextReader(
            MealRecordRepository mealRecordRepository,
            WorkoutRecordRepository workoutRecordRepository,
            DailyConditionRepository dailyConditionRepository
    ) {
        this.mealRecordRepository = mealRecordRepository;
        this.workoutRecordRepository = workoutRecordRepository;
        this.dailyConditionRepository = dailyConditionRepository;
    }

    public CoachingContext read(AppUser user, LocalDate targetDate) {
        LocalDate startDate = targetDate.minusDays(CONTEXT_DAYS - 1L);
        List<MealRecord> meals = mealRecordRepository.findByUser_IdAndRecordedOnBetweenOrderByRecordedOnDescIdDesc(
                user.getId(),
                startDate,
                targetDate
        );
        List<WorkoutRecord> workouts =
                workoutRecordRepository.findByUser_IdAndRecordedOnBetweenOrderByRecordedOnDescIdDesc(
                        user.getId(),
                        startDate,
                        targetDate
                );
        List<DailyCondition> conditions =
                dailyConditionRepository.findByUser_IdAndRecordedOnBetweenOrderByRecordedOnDesc(
                        user.getId(),
                        startDate,
                        targetDate
                );

        return new CoachingContext(startDate, targetDate, meals, workouts, conditions);
    }
}
