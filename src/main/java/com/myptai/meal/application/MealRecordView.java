package com.myptai.meal.application;

import com.myptai.meal.domain.MealRecord;
import com.myptai.meal.domain.MealType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MealRecordView(
        Long id,
        LocalDate recordedOn,
        MealType mealType,
        String name,
        Integer calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        String memo
) {

    public static MealRecordView from(MealRecord mealRecord) {
        return new MealRecordView(
                mealRecord.getId(),
                mealRecord.getRecordedOn(),
                mealRecord.getMealType(),
                mealRecord.getName(),
                mealRecord.getCalories(),
                mealRecord.getProteinG(),
                mealRecord.getCarbsG(),
                mealRecord.getFatG(),
                mealRecord.getMemo()
        );
    }
}
