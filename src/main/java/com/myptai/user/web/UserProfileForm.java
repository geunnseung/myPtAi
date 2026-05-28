package com.myptai.user.web;

import com.myptai.user.application.UserProfileCommand;
import com.myptai.user.application.UserProfileView;
import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserProfileForm {

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해 주세요.")
    private String displayName;

    @NotEmpty(message = "목표를 하나 이상 선택해 주세요.")
    private List<GoalType> goals = new ArrayList<>();

    @DecimalMin(value = "80.0", message = "키는 80cm 이상으로 입력해 주세요.")
    @DecimalMax(value = "230.0", message = "키는 230cm 이하로 입력해 주세요.")
    @Digits(integer = 3, fraction = 1, message = "키는 소수점 한 자리까지 입력해 주세요.")
    private BigDecimal heightCm;

    @DecimalMin(value = "25.0", message = "체중은 25kg 이상으로 입력해 주세요.")
    @DecimalMax(value = "250.0", message = "체중은 250kg 이하로 입력해 주세요.")
    private BigDecimal weightKg;

    @NotNull(message = "활동량을 선택해 주세요.")
    private ActivityLevel activityLevel;

    @Size(max = 500, message = "선호 식단은 500자 이하로 입력해 주세요.")
    private String foodPreference;

    @Size(max = 500, message = "제한사항은 500자 이하로 입력해 주세요.")
    private String restrictions;

    public static UserProfileForm empty() {
        UserProfileForm form = new UserProfileForm();
        form.goals = new ArrayList<>(List.of(GoalType.FAT_LOSS));
        form.activityLevel = ActivityLevel.MODERATE;
        return form;
    }

    public static UserProfileForm from(UserProfileView profile) {
        UserProfileForm form = new UserProfileForm();
        form.displayName = profile.displayName();
        form.goals = new ArrayList<>(profile.goals());
        form.heightCm = profile.heightCm();
        form.weightKg = profile.weightKg();
        form.activityLevel = profile.activityLevel();
        form.foodPreference = profile.foodPreference();
        form.restrictions = profile.restrictions();
        return form;
    }

    public UserProfileCommand toCommand() {
        return new UserProfileCommand(
                displayName,
                goals,
                heightCm,
                weightKg,
                activityLevel,
                foodPreference,
                restrictions
        );
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<GoalType> getGoals() {
        return goals;
    }

    public void setGoals(List<GoalType> goals) {
        this.goals = goals == null ? new ArrayList<>() : goals;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }
}
