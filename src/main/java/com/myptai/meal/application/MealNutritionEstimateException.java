package com.myptai.meal.application;

public class MealNutritionEstimateException extends RuntimeException {

    public MealNutritionEstimateException(String message) {
        super(message);
    }

    public MealNutritionEstimateException(String message, Throwable cause) {
        super(message, cause);
    }
}
