package com.myptai.workout.application;

public class RequiredUserProfileException extends RuntimeException {

    public RequiredUserProfileException() {
        super("운동 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }
}
