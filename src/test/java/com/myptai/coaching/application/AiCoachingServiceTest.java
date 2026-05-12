package com.myptai.coaching.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myptai.coaching.domain.AiCoachingStatus;
import com.myptai.coaching.repository.AiCoachingRequestRepository;
import com.myptai.condition.application.DailyConditionCommand;
import com.myptai.condition.application.DailyConditionService;
import com.myptai.condition.domain.EnergyLevel;
import com.myptai.meal.application.MealRecordCommand;
import com.myptai.meal.application.MealRecordService;
import com.myptai.meal.domain.MealType;
import com.myptai.user.application.UserProfileCommand;
import com.myptai.user.application.UserProfileService;
import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import com.myptai.workout.application.WorkoutRecordCommand;
import com.myptai.workout.application.WorkoutRecordService;
import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutType;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AiCoachingServiceTest {

    @Autowired
    private AiCoachingService aiCoachingService;

    @Autowired
    private AiCoachingRequestRepository aiCoachingRequestRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private MealRecordService mealRecordService;

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @Autowired
    private DailyConditionService dailyConditionService;

    @Autowired
    private FakeOpenAiClient fakeOpenAiClient;

    @BeforeEach
    void setUp() {
        fakeOpenAiClient.reset();
    }

    @Test
    void 프로필이_없으면_AI_코칭을_요청할_수_없다() {
        AiCoachingCommand command = new AiCoachingCommand(
                LocalDate.of(2026, 6, 22),
                "오늘 식단과 운동을 추천해줘"
        );

        assertThatThrownBy(() -> aiCoachingService.request(command))
                .isInstanceOf(RequiredUserProfileException.class)
                .hasMessage("AI 코칭을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }

    @Test
    void AI_코칭_응답을_성공_상태로_저장한다() {
        createProfile();
        LocalDate targetDate = LocalDate.of(2026, 6, 22);
        mealRecordService.create(mealCommand(targetDate));
        workoutRecordService.create(workoutCommand(targetDate));
        dailyConditionService.save(conditionCommand(targetDate));
        fakeOpenAiClient.answer = "오늘은 단백질을 충분히 먹고 하체 부담이 적은 운동을 추천합니다.";

        AiCoachingView coaching = aiCoachingService.request(new AiCoachingCommand(
                targetDate,
                "오늘 식단과 운동을 추천해줘"
        ));

        assertThat(coaching.status()).isEqualTo(AiCoachingStatus.SUCCEEDED);
        assertThat(coaching.answer()).contains("단백질");
        assertThat(coaching.model()).isEqualTo("test-model");
        assertThat(fakeOpenAiClient.receivedPrompt)
                .contains("현미밥과 닭가슴살")
                .contains("전신 근력 운동")
                .contains("무릎 통증");
    }

    @Test
    void AI_코칭_응답_생성에_실패하면_실패_상태로_저장한다() {
        createProfile();
        fakeOpenAiClient.exception = new OpenAiClientException("OPENAI_API_KEY가 설정되지 않았습니다.");

        AiCoachingView coaching = aiCoachingService.request(new AiCoachingCommand(
                LocalDate.of(2026, 6, 22),
                "오늘 식단과 운동을 추천해줘"
        ));

        assertThat(coaching.status()).isEqualTo(AiCoachingStatus.FAILED);
        assertThat(coaching.answer()).isNull();
        assertThat(coaching.errorMessage()).isEqualTo("OPENAI_API_KEY가 설정되지 않았습니다.");
        assertThat(aiCoachingRequestRepository.findById(coaching.id())).isPresent();
    }

    private void createProfile() {
        userProfileService.save(new UserProfileCommand(
                "민수",
                GoalType.FAT_LOSS,
                175,
                new BigDecimal("72.5"),
                ActivityLevel.MODERATE,
                "한식 위주",
                "무릎 통증"
        ));
    }

    private MealRecordCommand mealCommand(LocalDate recordedOn) {
        return new MealRecordCommand(
                recordedOn,
                MealType.BREAKFAST,
                "현미밥과 닭가슴살",
                650,
                new BigDecimal("35.0"),
                new BigDecimal("72.0"),
                new BigDecimal("16.0"),
                "든든하게 먹음"
        );
    }

    private WorkoutRecordCommand workoutCommand(LocalDate recordedOn) {
        return new WorkoutRecordCommand(
                recordedOn,
                WorkoutType.STRENGTH,
                "전신 근력 운동",
                60,
                WorkoutIntensity.NORMAL,
                "스쿼트는 가볍게 진행"
        );
    }

    private DailyConditionCommand conditionCommand(LocalDate recordedOn) {
        return new DailyConditionCommand(
                recordedOn,
                420,
                new BigDecimal("72.5"),
                EnergyLevel.NORMAL,
                "특이사항 없음"
        );
    }

    @TestConfiguration
    static class FakeOpenAiClientConfig {

        @Bean
        @Primary
        FakeOpenAiClient fakeOpenAiClient() {
            return new FakeOpenAiClient();
        }
    }

    static class FakeOpenAiClient implements OpenAiClient {

        private String answer = "기본 테스트 응답입니다.";
        private OpenAiClientException exception;
        private String receivedPrompt;

        @Override
        public String modelName() {
            return "test-model";
        }

        @Override
        public String createCoachingAnswer(String prompt) {
            this.receivedPrompt = prompt;
            if (exception != null) {
                throw exception;
            }
            return answer;
        }

        private void reset() {
            answer = "기본 테스트 응답입니다.";
            exception = null;
            receivedPrompt = null;
        }
    }
}
