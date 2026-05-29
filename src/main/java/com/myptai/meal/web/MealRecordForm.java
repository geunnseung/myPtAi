package com.myptai.meal.web;

import com.myptai.meal.application.MealRecordCommand;
import com.myptai.meal.application.MealRecordView;
import com.myptai.meal.application.MealNutritionEstimateView;
import com.myptai.meal.domain.MealType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MealRecordForm {

    @NotNull(message = "기록 날짜를 선택해 주세요.")
    private LocalDate recordedOn;

    @NotNull(message = "식사 구분을 선택해 주세요.")
    private MealType mealType;

    @NotBlank(message = "메뉴명을 입력해 주세요.")
    @Size(max = 100, message = "메뉴명은 100자 이하로 입력해 주세요.")
    private String name;

    @Min(value = 0, message = "칼로리는 0 이상으로 입력해 주세요.")
    @Max(value = 10000, message = "칼로리는 10000 이하로 입력해 주세요.")
    private Integer calories;

    @DecimalMin(value = "0.0", message = "단백질은 0g 이상으로 입력해 주세요.")
    @DecimalMax(value = "999.99", message = "단백질은 999.99g 이하로 입력해 주세요.")
    private BigDecimal proteinG;

    @DecimalMin(value = "0.0", message = "탄수화물은 0g 이상으로 입력해 주세요.")
    @DecimalMax(value = "999.99", message = "탄수화물은 999.99g 이하로 입력해 주세요.")
    private BigDecimal carbsG;

    @DecimalMin(value = "0.0", message = "지방은 0g 이상으로 입력해 주세요.")
    @DecimalMax(value = "999.99", message = "지방은 999.99g 이하로 입력해 주세요.")
    private BigDecimal fatG;

    @Size(max = 500, message = "메모는 500자 이하로 입력해 주세요.")
    private String memo;

    private String nutritionEstimateInput;

    public static MealRecordForm empty(LocalDate recordedOn) {
        MealRecordForm form = new MealRecordForm();
        form.recordedOn = recordedOn;
        form.mealType = MealType.BREAKFAST;
        return form;
    }

    public static MealRecordForm from(MealRecordView mealRecord) {
        MealRecordForm form = new MealRecordForm();
        form.recordedOn = mealRecord.recordedOn();
        form.mealType = mealRecord.mealType();
        form.name = mealRecord.name();
        form.calories = mealRecord.calories();
        form.proteinG = mealRecord.proteinG();
        form.carbsG = mealRecord.carbsG();
        form.fatG = mealRecord.fatG();
        form.memo = mealRecord.memo();
        return form;
    }

    public MealRecordCommand toCommand() {
        return new MealRecordCommand(recordedOn, mealType, name, calories, proteinG, carbsG, fatG, memo);
    }

    public void applyNutritionEstimate(MealNutritionEstimateView estimate) {
        calories = estimate.calories();
        proteinG = estimate.proteinG();
        carbsG = estimate.carbsG();
        fatG = estimate.fatG();
        if (name == null || name.isBlank()) {
            name = toMealName(nutritionEstimateInput);
        }
    }

    public LocalDate getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(LocalDate recordedOn) {
        this.recordedOn = recordedOn;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProteinG() {
        return proteinG;
    }

    public void setProteinG(BigDecimal proteinG) {
        this.proteinG = proteinG;
    }

    public BigDecimal getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(BigDecimal carbsG) {
        this.carbsG = carbsG;
    }

    public BigDecimal getFatG() {
        return fatG;
    }

    public void setFatG(BigDecimal fatG) {
        this.fatG = fatG;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getNutritionEstimateInput() {
        return nutritionEstimateInput;
    }

    public void setNutritionEstimateInput(String nutritionEstimateInput) {
        this.nutritionEstimateInput = nutritionEstimateInput;
    }

    private String toMealName(String value) {
        if (value == null || value.isBlank()) {
            return name;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() <= 100) {
            return normalized;
        }
        return normalized.substring(0, 100);
    }
}
