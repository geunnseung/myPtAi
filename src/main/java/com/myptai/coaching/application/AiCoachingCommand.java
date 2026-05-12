package com.myptai.coaching.application;

import java.time.LocalDate;

public record AiCoachingCommand(
        LocalDate targetDate,
        String question
) {
}
