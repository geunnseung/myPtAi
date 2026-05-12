package com.myptai.coaching.application;

import com.myptai.coaching.domain.AiCoachingRequest;
import com.myptai.coaching.domain.AiCoachingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AiCoachingView(
        Long id,
        LocalDate targetDate,
        String question,
        String answer,
        String model,
        AiCoachingStatus status,
        String errorMessage,
        LocalDateTime createdAt
) {

    public static AiCoachingView from(AiCoachingRequest request) {
        return new AiCoachingView(
                request.getId(),
                request.getTargetDate(),
                request.getQuestion(),
                request.getAnswer(),
                request.getModel(),
                request.getStatus(),
                request.getErrorMessage(),
                request.getCreatedAt()
        );
    }

    public boolean succeeded() {
        return status == AiCoachingStatus.SUCCEEDED;
    }

    public boolean failed() {
        return status == AiCoachingStatus.FAILED;
    }
}
