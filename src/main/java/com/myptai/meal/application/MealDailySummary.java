package com.myptai.meal.application;

import java.math.BigDecimal;
import java.util.List;

public record MealDailySummary(
        int totalCalories,
        BigDecimal totalProteinG,
        BigDecimal totalCarbsG,
        BigDecimal totalFatG
) {

    public static MealDailySummary from(List<MealRecordView> meals) {
        int calories = meals.stream()
                .map(MealRecordView::calories)
                .mapToInt(value -> value == null ? 0 : value)
                .sum();

        BigDecimal protein = meals.stream()
                .map(MealRecordView::proteinG)
                .reduce(BigDecimal.ZERO, MealDailySummary::addNullable);

        BigDecimal carbs = meals.stream()
                .map(MealRecordView::carbsG)
                .reduce(BigDecimal.ZERO, MealDailySummary::addNullable);

        BigDecimal fat = meals.stream()
                .map(MealRecordView::fatG)
                .reduce(BigDecimal.ZERO, MealDailySummary::addNullable);

        return new MealDailySummary(calories, protein, carbs, fat);
    }

    private static BigDecimal addNullable(BigDecimal left, BigDecimal right) {
        return right == null ? left : left.add(right);
    }
}
