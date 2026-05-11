package com.myptai.condition.application;

import java.time.LocalDate;

public class DailyConditionNotFoundException extends RuntimeException {

    public DailyConditionNotFoundException(LocalDate recordedOn) {
        super("하루 컨디션 기록을 찾을 수 없습니다. recordedOn=" + recordedOn);
    }
}
