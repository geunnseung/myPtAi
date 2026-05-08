package com.myptai.meal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MealRecordCommandValues(
        LocalDate recordedOn,
        MealType mealType,
        String name,
        Integer calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        String memo
) {
}
