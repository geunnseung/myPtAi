package com.myptai.condition.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyConditionCommandValues(
        LocalDate recordedOn,
        Integer sleepMinutes,
        BigDecimal bodyWeightKg,
        EnergyLevel energyLevel,
        String memo
) {
}
