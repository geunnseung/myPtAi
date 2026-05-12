package com.myptai.coaching.domain;

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
@Table(name = "ai_coaching_request")
public class AiCoachingRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "question", nullable = false, length = 1000)
    private String question;

    @Column(name = "answer", length = 10000)
    private String answer;

    @Column(name = "model", length = 100)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AiCoachingStatus status;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    protected AiCoachingRequest() {
    }

    private AiCoachingRequest(AppUser user, LocalDate targetDate, String question, String model) {
        this.user = user;
        this.targetDate = targetDate;
        this.question = question;
        this.model = model;
        this.status = AiCoachingStatus.REQUESTED;
    }

    public static AiCoachingRequest request(AppUser user, LocalDate targetDate, String question, String model) {
        return new AiCoachingRequest(user, targetDate, question, model);
    }

    public void succeed(String answer) {
        this.answer = trimAnswer(answer);
        this.errorMessage = null;
        this.status = AiCoachingStatus.SUCCEEDED;
    }

    public void fail(String errorMessage) {
        this.answer = null;
        this.errorMessage = trimErrorMessage(errorMessage);
        this.status = AiCoachingStatus.FAILED;
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getModel() {
        return model;
    }

    public AiCoachingStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String trimErrorMessage(String value) {
        if (value == null || value.isBlank()) {
            return "AI 코칭 응답을 생성하지 못했습니다.";
        }
        return value.length() > 1000 ? value.substring(0, 1000) : value;
    }

    private String trimAnswer(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > 10000 ? value.substring(0, 10000) : value;
    }
}
