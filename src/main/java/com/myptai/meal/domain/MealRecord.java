package com.myptai.meal.domain;

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
@Table(name = "meal_record")
public class MealRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "recorded_on", nullable = false)
    private LocalDate recordedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 30)
    private MealType mealType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "protein_g", precision = 6, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 6, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 6, scale = 2)
    private BigDecimal fatG;

    @Column(name = "memo", length = 500)
    private String memo;

    protected MealRecord() {
    }

    private MealRecord(
            AppUser user,
            LocalDate recordedOn,
            MealType mealType,
            String name,
            Integer calories,
            BigDecimal proteinG,
            BigDecimal carbsG,
            BigDecimal fatG,
            String memo
    ) {
        this.user = user;
        this.recordedOn = recordedOn;
        this.mealType = mealType;
        this.name = name;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
        this.memo = memo;
    }

    public static MealRecord create(AppUser user, MealRecordCommandValues values) {
        return new MealRecord(
                user,
                values.recordedOn(),
                values.mealType(),
                values.name(),
                values.calories(),
                values.proteinG(),
                values.carbsG(),
                values.fatG(),
                values.memo()
        );
    }

    public void update(MealRecordCommandValues values) {
        this.recordedOn = values.recordedOn();
        this.mealType = values.mealType();
        this.name = values.name();
        this.calories = values.calories();
        this.proteinG = values.proteinG();
        this.carbsG = values.carbsG();
        this.fatG = values.fatG();
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

    public MealType getMealType() {
        return mealType;
    }

    public String getName() {
        return name;
    }

    public Integer getCalories() {
        return calories;
    }

    public BigDecimal getProteinG() {
        return proteinG;
    }

    public BigDecimal getCarbsG() {
        return carbsG;
    }

    public BigDecimal getFatG() {
        return fatG;
    }

    public String getMemo() {
        return memo;
    }
}
