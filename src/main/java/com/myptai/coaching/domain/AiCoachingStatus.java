package com.myptai.coaching.domain;

public enum AiCoachingStatus {
    REQUESTED("요청됨"),
    SUCCEEDED("성공"),
    FAILED("실패");

    private final String label;

    AiCoachingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
