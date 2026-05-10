package com.myptai.workout.web;

import com.myptai.workout.application.WorkoutRecordCommand;
import com.myptai.workout.application.WorkoutRecordView;
import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class WorkoutRecordForm {

    @NotNull(message = "기록 날짜를 선택해 주세요.")
    private LocalDate recordedOn;

    @NotNull(message = "운동 종류를 선택해 주세요.")
    private WorkoutType workoutType;

    @NotBlank(message = "운동명을 입력해 주세요.")
    @Size(max = 100, message = "운동명은 100자 이하로 입력해 주세요.")
    private String title;

    @Min(value = 0, message = "운동 시간은 0분 이상으로 입력해 주세요.")
    @Max(value = 1440, message = "운동 시간은 1440분 이하로 입력해 주세요.")
    private Integer durationMinutes;

    @NotNull(message = "운동 강도를 선택해 주세요.")
    private WorkoutIntensity intensity;

    @Size(max = 1000, message = "메모는 1000자 이하로 입력해 주세요.")
    private String memo;

    public static WorkoutRecordForm empty(LocalDate recordedOn) {
        WorkoutRecordForm form = new WorkoutRecordForm();
        form.recordedOn = recordedOn;
        form.workoutType = WorkoutType.STRENGTH;
        form.intensity = WorkoutIntensity.NORMAL;
        return form;
    }

    public static WorkoutRecordForm from(WorkoutRecordView workoutRecord) {
        WorkoutRecordForm form = new WorkoutRecordForm();
        form.recordedOn = workoutRecord.recordedOn();
        form.workoutType = workoutRecord.workoutType();
        form.title = workoutRecord.title();
        form.durationMinutes = workoutRecord.durationMinutes();
        form.intensity = workoutRecord.intensity();
        form.memo = workoutRecord.memo();
        return form;
    }

    public WorkoutRecordCommand toCommand() {
        return new WorkoutRecordCommand(recordedOn, workoutType, title, durationMinutes, intensity, memo);
    }

    public LocalDate getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(LocalDate recordedOn) {
        this.recordedOn = recordedOn;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public WorkoutIntensity getIntensity() {
        return intensity;
    }

    public void setIntensity(WorkoutIntensity intensity) {
        this.intensity = intensity;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
