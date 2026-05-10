package com.myptai.workout.application;

public class WorkoutRecordNotFoundException extends RuntimeException {

    public WorkoutRecordNotFoundException(Long workoutRecordId) {
        super("운동 기록을 찾을 수 없습니다. id=" + workoutRecordId);
    }
}
