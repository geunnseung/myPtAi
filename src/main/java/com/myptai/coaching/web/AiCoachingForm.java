package com.myptai.coaching.web;

import com.myptai.coaching.application.AiCoachingCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AiCoachingForm {

    @NotNull(message = "기준 날짜를 선택해 주세요.")
    private LocalDate targetDate;

    @NotBlank(message = "AI에게 물어볼 내용을 입력해 주세요.")
    @Size(max = 1000, message = "질문은 1000자 이하로 입력해 주세요.")
    private String question;

    public static AiCoachingForm empty(LocalDate targetDate) {
        AiCoachingForm form = new AiCoachingForm();
        form.targetDate = targetDate;
        return form;
    }

    public AiCoachingCommand toCommand() {
        return new AiCoachingCommand(targetDate, question);
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
