package com.myptai.meal.application;

public class RequiredUserProfileException extends RuntimeException {

    public RequiredUserProfileException() {
        super("식단 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }
}
