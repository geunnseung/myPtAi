package com.myptai.condition.web;

import com.myptai.condition.application.DailyConditionCommand;
import com.myptai.condition.application.DailyConditionView;
import com.myptai.condition.domain.EnergyLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyConditionForm {

    @NotNull(message = "기록 날짜를 선택해 주세요.")
    private LocalDate recordedOn;

    @Min(value = 0, message = "수면 시간은 0분 이상으로 입력해 주세요.")
    @Max(value = 1440, message = "수면 시간은 1440분 이하로 입력해 주세요.")
    private Integer sleepMinutes;

    @DecimalMin(value = "25.0", message = "체중은 25kg 이상으로 입력해 주세요.")
    @DecimalMax(value = "250.0", message = "체중은 250kg 이하로 입력해 주세요.")
    private BigDecimal bodyWeightKg;

    private EnergyLevel energyLevel;

    @Size(max = 1000, message = "메모는 1000자 이하로 입력해 주세요.")
    private String memo;

    public static DailyConditionForm empty(LocalDate recordedOn) {
        DailyConditionForm form = new DailyConditionForm();
        form.recordedOn = recordedOn;
        form.energyLevel = EnergyLevel.NORMAL;
        return form;
    }

    public static DailyConditionForm from(DailyConditionView condition) {
        DailyConditionForm form = new DailyConditionForm();
        form.recordedOn = condition.recordedOn();
        form.sleepMinutes = condition.sleepMinutes();
        form.bodyWeightKg = condition.bodyWeightKg();
        form.energyLevel = condition.energyLevel();
        form.memo = condition.memo();
        return form;
    }

    public DailyConditionCommand toCommand() {
        return new DailyConditionCommand(recordedOn, sleepMinutes, bodyWeightKg, energyLevel, memo);
    }

    public LocalDate getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(LocalDate recordedOn) {
        this.recordedOn = recordedOn;
    }

    public Integer getSleepMinutes() {
        return sleepMinutes;
    }

    public void setSleepMinutes(Integer sleepMinutes) {
        this.sleepMinutes = sleepMinutes;
    }

    public BigDecimal getBodyWeightKg() {
        return bodyWeightKg;
    }

    public void setBodyWeightKg(BigDecimal bodyWeightKg) {
        this.bodyWeightKg = bodyWeightKg;
    }

    public EnergyLevel getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(EnergyLevel energyLevel) {
        this.energyLevel = energyLevel;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
