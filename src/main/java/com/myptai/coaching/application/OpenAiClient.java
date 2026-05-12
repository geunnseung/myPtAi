package com.myptai.coaching.application;

public interface OpenAiClient {

    String modelName();

    String createCoachingAnswer(String prompt);
}
