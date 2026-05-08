package com.myptai.meal.application;

import com.myptai.meal.domain.MealRecordCommandValues;
import com.myptai.meal.domain.MealType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MealRecordCommand(
        LocalDate recordedOn,
        MealType mealType,
        String name,
        Integer calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        String memo
) {

    public MealRecordCommandValues toValues() {
        return new MealRecordCommandValues(recordedOn, mealType, name, calories, proteinG, carbsG, fatG, memo);
    }
}
