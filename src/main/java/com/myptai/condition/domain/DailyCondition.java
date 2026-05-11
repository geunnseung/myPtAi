package com.myptai.condition.domain;

import com.myptai.global.domain.BaseTimeEntity;
import com.myptai.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_condition")
public class DailyCondition extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "recorded_on", nullable = false)
    private LocalDate recordedOn;

    @Column(name = "sleep_minutes")
    private Integer sleepMinutes;

    @Column(name = "body_weight_kg", precision = 5, scale = 2)
    private BigDecimal bodyWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "energy_level", length = 30)
    private EnergyLevel energyLevel;

    @Column(name = "memo", length = 1000)
    private String memo;

    protected DailyCondition() {
    }

    private DailyCondition(AppUser user, DailyConditionCommandValues values) {
        this.user = user;
        this.recordedOn = values.recordedOn();
        this.sleepMinutes = values.sleepMinutes();
        this.bodyWeightKg = values.bodyWeightKg();
        this.energyLevel = values.energyLevel();
        this.memo = values.memo();
    }

    public static DailyCondition create(AppUser user, DailyConditionCommandValues values) {
        return new DailyCondition(user, values);
    }

    public void update(DailyConditionCommandValues values) {
        this.recordedOn = values.recordedOn();
        this.sleepMinutes = values.sleepMinutes();
        this.bodyWeightKg = values.bodyWeightKg();
        this.energyLevel = values.energyLevel();
        this.memo = values.memo();
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public LocalDate getRecordedOn() {
        return recordedOn;
    }

    public Integer getSleepMinutes() {
        return sleepMinutes;
    }

    public BigDecimal getBodyWeightKg() {
        return bodyWeightKg;
    }

    public EnergyLevel getEnergyLevel() {
        return energyLevel;
    }

    public String getMemo() {
        return memo;
    }
}
