package com.myptai.auth.application;

import com.myptai.user.domain.GoalType;

public record SignupCommand(
        String email,
        String password,
        String displayName,
        GoalType goal
) {

    public String normalizedEmail() {
        return email == null ? null : email.trim().toLowerCase();
    }
}
