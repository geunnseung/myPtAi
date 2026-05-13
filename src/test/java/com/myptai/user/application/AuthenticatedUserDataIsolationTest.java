package com.myptai.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.myptai.auth.application.SignupCommand;
import com.myptai.auth.application.SignupService;
import com.myptai.coaching.application.AiCoachingCommand;
import com.myptai.coaching.application.AiCoachingService;
import com.myptai.coaching.application.AiCoachingView;
import com.myptai.coaching.application.OpenAiClient;
import com.myptai.condition.application.DailyConditionCommand;
import com.myptai.condition.application.DailyConditionService;
import com.myptai.condition.application.DailyConditionView;
import com.myptai.condition.domain.EnergyLevel;
import com.myptai.meal.application.MealRecordCommand;
import com.myptai.meal.application.MealRecordService;
import com.myptai.meal.application.MealRecordView;
import com.myptai.meal.domain.MealType;
import com.myptai.user.domain.GoalType;
import com.myptai.workout.application.WorkoutRecordCommand;
import com.myptai.workout.application.WorkoutRecordService;
import com.myptai.workout.application.WorkoutRecordView;
import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthenticatedUserDataIsolationTest {

    private static final LocalDate RECORDED_ON = LocalDate.of(2026, 6, 22);

    @Autowired
    private SignupService signupService;

    @Autowired
    private MealRecordService mealRecordService;

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @Autowired
    private DailyConditionService dailyConditionService;

    @Autowired
    private AiCoachingService aiCoachingService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 로그인한_사용자의_기록만_조회한다() {
        signup("minsu@example.com", "민수");
        signup("jiyoon@example.com", "지윤");

        authenticate("minsu@example.com");
        mealRecordService.create(mealCommand("민수 아침"));
        workoutRecordService.create(workoutCommand("민수 상체 운동"));
        dailyConditionService.save(conditionCommand(420, "72.5", "민수 컨디션"));
        aiCoachingService.request(new AiCoachingCommand(RECORDED_ON, "민수 오늘 계획"));

        authenticate("jiyoon@example.com");
        mealRecordService.create(mealCommand("지윤 아침"));
        workoutRecordService.create(workoutCommand("지윤 유산소"));
        dailyConditionService.save(conditionCommand(390, "58.2", "지윤 컨디션"));
        aiCoachingService.request(new AiCoachingCommand(RECORDED_ON, "지윤 오늘 계획"));

        authenticate("minsu@example.com");
        assertThat(mealRecordService.findByDate(RECORDED_ON))
                .extracting(MealRecordView::name)
                .containsExactly("민수 아침");
        assertThat(workoutRecordService.findByDate(RECORDED_ON))
                .extracting(WorkoutRecordView::title)
                .containsExactly("민수 상체 운동");
        assertThat(dailyConditionService.findByDate(RECORDED_ON))
                .map(DailyConditionView::memo)
                .contains("민수 컨디션");
        assertThat(aiCoachingService.findByDate(RECORDED_ON))
                .extracting(AiCoachingView::question)
                .containsExactly("민수 오늘 계획");

        authenticate("jiyoon@example.com");
        assertThat(mealRecordService.findByDate(RECORDED_ON))
                .extracting(MealRecordView::name)
                .containsExactly("지윤 아침");
        assertThat(workoutRecordService.findByDate(RECORDED_ON))
                .extracting(WorkoutRecordView::title)
                .containsExactly("지윤 유산소");
        assertThat(dailyConditionService.findByDate(RECORDED_ON))
                .map(DailyConditionView::memo)
                .contains("지윤 컨디션");
        assertThat(aiCoachingService.findByDate(RECORDED_ON))
                .extracting(AiCoachingView::question)
                .containsExactly("지윤 오늘 계획");
    }

    private void signup(String email, String displayName) {
        signupService.signup(new SignupCommand(email, "password123", displayName, GoalType.FAT_LOSS));
    }

    private void authenticate(String email) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email,
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private MealRecordCommand mealCommand(String name) {
        return new MealRecordCommand(
                RECORDED_ON,
                MealType.BREAKFAST,
                name,
                650,
                new BigDecimal("35.0"),
                new BigDecimal("72.0"),
                new BigDecimal("16.0"),
                "아침"
        );
    }

    private WorkoutRecordCommand workoutCommand(String title) {
        return new WorkoutRecordCommand(
                RECORDED_ON,
                WorkoutType.STRENGTH,
                title,
                60,
                WorkoutIntensity.NORMAL,
                "운동 메모"
        );
    }

    private DailyConditionCommand conditionCommand(int sleepMinutes, String bodyWeightKg, String memo) {
        return new DailyConditionCommand(
                RECORDED_ON,
                sleepMinutes,
                new BigDecimal(bodyWeightKg),
                EnergyLevel.NORMAL,
                memo
        );
    }

    @TestConfiguration
    static class FakeOpenAiClientConfig {

        @Bean
        @Primary
        OpenAiClient openAiClient() {
            return new OpenAiClient() {
                @Override
                public String modelName() {
                    return "test-model";
                }

                @Override
                public String createCoachingAnswer(String prompt) {
                    return "테스트 코칭 응답";
                }
            };
        }
    }
}
