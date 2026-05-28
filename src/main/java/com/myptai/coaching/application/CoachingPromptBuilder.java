package com.myptai.coaching.application;

import com.myptai.condition.domain.DailyCondition;
import com.myptai.meal.domain.MealRecord;
import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.workout.domain.WorkoutRecord;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CoachingPromptBuilder {

    private final CoachingContextReader coachingContextReader;

    public CoachingPromptBuilder(CoachingContextReader coachingContextReader) {
        this.coachingContextReader = coachingContextReader;
    }

    public String build(AppUser user, AiCoachingCommand command) {
        CoachingContext context = coachingContextReader.read(user, command.targetDate());

        StringBuilder prompt = new StringBuilder();
        appendProfile(prompt, user);
        prompt.append("\n[요청]\n")
                .append("- 기준 날짜: ").append(command.targetDate()).append('\n')
                .append("- 사용자 질문: ").append(command.question()).append('\n');
        appendMeals(prompt, context.meals());
        appendWorkouts(prompt, context.workouts());
        appendConditions(prompt, context.conditions());
        prompt.append("""

                [답변 형식]
                - 오늘의 핵심 판단
                - 식단 제안
                - 운동 제안
                - 주의할 점
                """);
        return prompt.toString();
    }

    private void appendProfile(StringBuilder prompt, AppUser user) {
        prompt.append("[프로필]\n")
                .append("- 이름: ").append(user.getDisplayName()).append('\n')
                .append("- 목표: ").append(goalLabels(user.getGoals())).append('\n')
                .append("- 키: ").append(valueOrDash(user.getHeightCm())).append("cm\n")
                .append("- 체중: ").append(valueOrDash(user.getWeightKg())).append("kg\n")
                .append("- 활동량: ").append(user.getActivityLevel() == null ? "-" : user.getActivityLevel().getLabel()).append('\n')
                .append("- 식사 선호: ").append(valueOrDash(user.getFoodPreference())).append('\n')
                .append("- 제한/주의사항: ").append(valueOrDash(user.getRestrictions())).append('\n');
    }

    private void appendMeals(StringBuilder prompt, List<MealRecord> meals) {
        prompt.append("\n[최근 식단]\n");
        if (meals.isEmpty()) {
            prompt.append("- 기록 없음\n");
            return;
        }
        for (MealRecord meal : meals) {
            prompt.append("- ")
                    .append(meal.getRecordedOn()).append(" ")
                    .append(meal.getMealType().getLabel()).append(": ")
                    .append(meal.getName())
                    .append(", 열량 ").append(valueOrDash(meal.getCalories())).append("kcal")
                    .append(", 단백질 ").append(valueOrDash(meal.getProteinG())).append("g")
                    .append(", 탄수화물 ").append(valueOrDash(meal.getCarbsG())).append("g")
                    .append(", 지방 ").append(valueOrDash(meal.getFatG())).append("g");
            appendMemo(prompt, meal.getMemo());
        }
    }

    private void appendWorkouts(StringBuilder prompt, List<WorkoutRecord> workouts) {
        prompt.append("\n[최근 운동]\n");
        if (workouts.isEmpty()) {
            prompt.append("- 기록 없음\n");
            return;
        }
        for (WorkoutRecord workout : workouts) {
            prompt.append("- ")
                    .append(workout.getRecordedOn()).append(" ")
                    .append(workout.getWorkoutType().getLabel()).append(": ")
                    .append(workout.getTitle())
                    .append(", 시간 ").append(valueOrDash(workout.getDurationMinutes())).append("분")
                    .append(", 강도 ")
                    .append(workout.getIntensity() == null ? "-" : workout.getIntensity().getLabel());
            appendMemo(prompt, workout.getMemo());
        }
    }

    private void appendConditions(StringBuilder prompt, List<DailyCondition> conditions) {
        prompt.append("\n[최근 컨디션]\n");
        if (conditions.isEmpty()) {
            prompt.append("- 기록 없음\n");
            return;
        }
        for (DailyCondition condition : conditions) {
            prompt.append("- ")
                    .append(condition.getRecordedOn())
                    .append(": 수면 ").append(valueOrDash(condition.getSleepMinutes())).append("분")
                    .append(", 체중 ").append(valueOrDash(condition.getBodyWeightKg())).append("kg")
                    .append(", 컨디션 ")
                    .append(condition.getEnergyLevel() == null ? "-" : condition.getEnergyLevel().getLabel());
            appendMemo(prompt, condition.getMemo());
        }
    }

    private void appendMemo(StringBuilder prompt, String memo) {
        if (memo == null || memo.isBlank()) {
            prompt.append('\n');
            return;
        }
        prompt.append(", 메모 ").append(memo).append('\n');
    }

    private String valueOrDash(Object value) {
        return value == null ? "-" : value.toString();
    }

    private String goalLabels(List<GoalType> goals) {
        if (goals == null || goals.isEmpty()) {
            return "-";
        }
        return goals.stream()
                .map(GoalType::getLabel)
                .collect(Collectors.joining(", "));
    }
}
