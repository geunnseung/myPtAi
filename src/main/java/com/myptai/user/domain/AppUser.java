package com.myptai.user.domain;

import com.myptai.global.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "app_user")
public class AppUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal", nullable = false, length = 50)
    private GoalType goal;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 30)
    private ActivityLevel activityLevel;

    @Column(name = "food_preference", length = 500)
    private String foodPreference;

    @Column(name = "restrictions", length = 500)
    private String restrictions;

    protected AppUser() {
    }

    private AppUser(
            String displayName,
            GoalType goal,
            Integer heightCm,
            BigDecimal weightKg,
            ActivityLevel activityLevel,
            String foodPreference,
            String restrictions
    ) {
        this.displayName = displayName;
        this.goal = goal;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.foodPreference = foodPreference;
        this.restrictions = restrictions;
    }

    public static AppUser create(
            String displayName,
            GoalType goal,
            Integer heightCm,
            BigDecimal weightKg,
            ActivityLevel activityLevel,
            String foodPreference,
            String restrictions
    ) {
        return new AppUser(displayName, goal, heightCm, weightKg, activityLevel, foodPreference, restrictions);
    }

    public void updateProfile(
            String displayName,
            GoalType goal,
            Integer heightCm,
            BigDecimal weightKg,
            ActivityLevel activityLevel,
            String foodPreference,
            String restrictions
    ) {
        this.displayName = displayName;
        this.goal = goal;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.foodPreference = foodPreference;
        this.restrictions = restrictions;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public GoalType getGoal() {
        return goal;
    }

    public Integer getHeightCm() {
        return heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public String getRestrictions() {
        return restrictions;
    }
}
