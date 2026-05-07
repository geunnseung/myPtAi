package com.myptai.user.application;

import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import java.math.BigDecimal;

public record UserProfileCommand(
        String displayName,
        GoalType goal,
        Integer heightCm,
        BigDecimal weightKg,
        ActivityLevel activityLevel,
        String foodPreference,
        String restrictions
) {
}
