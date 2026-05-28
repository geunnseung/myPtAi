package com.myptai.user.application;

import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import java.math.BigDecimal;
import java.util.List;

public record UserProfileView(
        Long id,
        String displayName,
        List<GoalType> goals,
        BigDecimal heightCm,
        BigDecimal weightKg,
        ActivityLevel activityLevel,
        String foodPreference,
        String restrictions
) {

    public static UserProfileView from(AppUser user) {
        return new UserProfileView(
                user.getId(),
                user.getDisplayName(),
                user.getGoals(),
                user.getHeightCm(),
                user.getWeightKg(),
                user.getActivityLevel(),
                user.getFoodPreference(),
                user.getRestrictions()
        );
    }
}
