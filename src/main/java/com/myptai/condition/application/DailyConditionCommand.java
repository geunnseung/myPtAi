package com.myptai.condition.application;

import com.myptai.condition.domain.DailyConditionCommandValues;
import com.myptai.condition.domain.EnergyLevel;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyConditionCommand(
        LocalDate recordedOn,
        Integer sleepMinutes,
        BigDecimal bodyWeightKg,
        EnergyLevel energyLevel,
        String memo
) {

    public DailyConditionCommandValues toValues() {
        return new DailyConditionCommandValues(recordedOn, sleepMinutes, bodyWeightKg, energyLevel, memo);
    }
}
