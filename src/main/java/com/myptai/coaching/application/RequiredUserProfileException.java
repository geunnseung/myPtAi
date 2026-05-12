package com.myptai.coaching.application;

public class RequiredUserProfileException extends RuntimeException {

    public RequiredUserProfileException() {
        super("AI 코칭을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }
}
