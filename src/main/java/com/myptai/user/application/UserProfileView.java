package com.myptai.user.application;

import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import java.math.BigDecimal;

public record UserProfileView(
        Long id,
        String displayName,
        GoalType goal,
        Integer heightCm,
        BigDecimal weightKg,
        ActivityLevel activityLevel,
        String foodPreference,
        String restrictions
) {

    public static UserProfileView from(AppUser user) {
        return new UserProfileView(
                user.getId(),
                user.getDisplayName(),
                user.getGoal(),
                user.getHeightCm(),
                user.getWeightKg(),
                user.getActivityLevel(),
                user.getFoodPreference(),
                user.getRestrictions()
        );
    }
}
