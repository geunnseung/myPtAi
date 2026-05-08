package com.myptai.meal.application;

public class MealRecordNotFoundException extends RuntimeException {

    public MealRecordNotFoundException(Long mealRecordId) {
        super("식단 기록을 찾을 수 없습니다. id=" + mealRecordId);
    }
}
