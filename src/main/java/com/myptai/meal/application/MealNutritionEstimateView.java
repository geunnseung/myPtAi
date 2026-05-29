package com.myptai.meal.application;

import java.math.BigDecimal;

public record MealNutritionEstimateView(
        Integer calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG
) {
}
