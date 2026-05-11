package com.myptai.condition.application;

public class RequiredUserProfileException extends RuntimeException {

    public RequiredUserProfileException() {
        super("컨디션 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }
}
