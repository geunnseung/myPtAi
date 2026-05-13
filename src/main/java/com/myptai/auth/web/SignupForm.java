package com.myptai.auth.web;

import com.myptai.auth.application.SignupCommand;
import com.myptai.user.domain.GoalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignupForm {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식으로 입력해 주세요.")
    @Size(max = 255, message = "이메일은 255자 이하로 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 72, message = "비밀번호는 8자 이상 72자 이하로 입력해 주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해 주세요.")
    private String displayName;

    @NotNull(message = "목표를 선택해 주세요.")
    private GoalType goal;

    public static SignupForm empty() {
        SignupForm form = new SignupForm();
        form.goal = GoalType.FAT_LOSS;
        return form;
    }

    public SignupCommand toCommand() {
        return new SignupCommand(email, password, displayName, goal);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GoalType getGoal() {
        return goal;
    }

    public void setGoal(GoalType goal) {
        this.goal = goal;
    }
}
