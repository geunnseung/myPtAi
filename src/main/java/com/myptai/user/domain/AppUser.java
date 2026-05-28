package com.myptai.user.domain;

import com.myptai.global.domain.BaseTimeEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
public class AppUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_user_goal", joinColumns = @JoinColumn(name = "app_user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "goal", nullable = false, length = 50)
    @OrderColumn(name = "sort_order")
    private List<GoalType> goals = new ArrayList<>();

    @Column(name = "height_cm", precision = 4, scale = 1)
    private BigDecimal heightCm;

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
            String email,
            String passwordHash,
            UserRole role,
            String displayName,
            List<GoalType> goals,
            BigDecimal heightCm,
            BigDecimal weightKg,
            ActivityLevel activityLevel,
            String foodPreference,
            String restrictions
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.displayName = displayName;
        this.goals = new ArrayList<>(normalizeGoals(goals));
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.foodPreference = foodPreference;
        this.restrictions = restrictions;
    }

    public static AppUser create(
            String displayName,
            List<GoalType> goals,
            BigDecimal heightCm,
            BigDecimal weightKg,
            ActivityLevel activityLevel,
            String foodPreference,
            String restrictions
    ) {
        return new AppUser(
                null,
                null,
                UserRole.USER,
                displayName,
                goals,
                heightCm,
                weightKg,
                activityLevel,
                foodPreference,
                restrictions
        );
    }

    public static AppUser signup(String email, String passwordHash, String displayName, GoalType goal) {
        return new AppUser(
                email,
                passwordHash,
                UserRole.USER,
                displayName,
                List.of(goal),
                null,
                null,
                ActivityLevel.MODERATE,
                null,
                null
        );
    }

    public boolean hasLoginCredentials() {
        return email != null && !email.isBlank() && passwordHash != null && !passwordHash.isBlank();
    }

    public void updateProfile(
            String displayName,
            List<GoalType> goals,
            BigDecimal heightCm,
            BigDecimal weightKg,
            ActivityLevel activityLevel,
            String foodPreference,
            String restrictions
    ) {
        this.displayName = displayName;
        this.goals.clear();
        this.goals.addAll(normalizeGoals(goals));
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.foodPreference = foodPreference;
        this.restrictions = restrictions;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<GoalType> getGoals() {
        return List.copyOf(goals);
    }

    public BigDecimal getHeightCm() {
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

    private List<GoalType> normalizeGoals(List<GoalType> values) {
        if (values == null || values.isEmpty()) {
            return List.of(GoalType.FAT_LOSS);
        }
        List<GoalType> normalizedGoals = values.stream()
                .filter(value -> value != null)
                .distinct()
                .toList();
        return normalizedGoals.isEmpty() ? List.of(GoalType.FAT_LOSS) : normalizedGoals;
    }
}
