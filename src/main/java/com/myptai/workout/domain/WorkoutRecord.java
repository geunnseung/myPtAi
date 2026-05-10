package com.myptai.workout.domain;

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
import java.time.LocalDate;

@Entity
@Table(name = "workout_record")
public class WorkoutRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "recorded_on", nullable = false)
    private LocalDate recordedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type", nullable = false, length = 30)
    private WorkoutType workoutType;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", length = 30)
    private WorkoutIntensity intensity;

    @Column(name = "memo", length = 1000)
    private String memo;

    protected WorkoutRecord() {
    }

    private WorkoutRecord(
            AppUser user,
            LocalDate recordedOn,
            WorkoutType workoutType,
            String title,
            Integer durationMinutes,
            WorkoutIntensity intensity,
            String memo
    ) {
        this.user = user;
        this.recordedOn = recordedOn;
        this.workoutType = workoutType;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.memo = memo;
    }

    public static WorkoutRecord create(AppUser user, WorkoutRecordCommandValues values) {
        return new WorkoutRecord(
                user,
                values.recordedOn(),
                values.workoutType(),
                values.title(),
                values.durationMinutes(),
                values.intensity(),
                values.memo()
        );
    }

    public void update(WorkoutRecordCommandValues values) {
        this.recordedOn = values.recordedOn();
        this.workoutType = values.workoutType();
        this.title = values.title();
        this.durationMinutes = values.durationMinutes();
        this.intensity = values.intensity();
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

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public String getTitle() {
        return title;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public WorkoutIntensity getIntensity() {
        return intensity;
    }

    public String getMemo() {
        return memo;
    }
}
