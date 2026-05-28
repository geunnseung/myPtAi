package com.myptai.user.application;

import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import java.math.BigDecimal;
import java.util.List;

public record UserProfileCommand(
        String displayName,
        List<GoalType> goals,
        BigDecimal heightCm,
        BigDecimal weightKg,
        ActivityLevel activityLevel,
        String foodPreference,
        String restrictions
) {
}
