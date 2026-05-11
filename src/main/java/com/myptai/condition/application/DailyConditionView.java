package com.myptai.condition.application;

import com.myptai.condition.domain.DailyCondition;
import com.myptai.condition.domain.EnergyLevel;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyConditionView(
        Long id,
        LocalDate recordedOn,
        Integer sleepMinutes,
        BigDecimal bodyWeightKg,
        EnergyLevel energyLevel,
        String memo
) {

    public static DailyConditionView from(DailyCondition dailyCondition) {
        return new DailyConditionView(
                dailyCondition.getId(),
                dailyCondition.getRecordedOn(),
                dailyCondition.getSleepMinutes(),
                dailyCondition.getBodyWeightKg(),
                dailyCondition.getEnergyLevel(),
                dailyCondition.getMemo()
        );
    }
}
