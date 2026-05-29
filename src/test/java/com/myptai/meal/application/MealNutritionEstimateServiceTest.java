package com.myptai.meal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myptai.user.application.UserProfileCommand;
import com.myptai.user.application.UserProfileService;
import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import java.math.BigDecimal;
import java.util.List;
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
class MealNutritionEstimateServiceTest {

    @Autowired
    private MealNutritionEstimateService mealNutritionEstimateService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private FakeMealNutritionAiClient fakeMealNutritionAiClient;

    @BeforeEach
    void setUp() {
        fakeMealNutritionAiClient.reset();
    }

    @Test
    void 프로필이_없으면_AI_영양_계산을_요청할_수_없다() {
        MealNutritionEstimateCommand command = new MealNutritionEstimateCommand("현미밥 150g");

        assertThatThrownBy(() -> mealNutritionEstimateService.estimate(command))
                .isInstanceOf(RequiredUserProfileException.class)
                .hasMessage("식단 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }

    @Test
    void 음식과_중량을_바탕으로_영양성분을_계산한다() {
        createProfile();
        fakeMealNutritionAiClient.response = """
                {"calories": 521, "protein_g": 38.24, "carbs_g": 70.04, "fat_g": 8.86}
                """;

        MealNutritionEstimateView estimate = mealNutritionEstimateService.estimate(
                new MealNutritionEstimateCommand("현미밥 150g, 닭가슴살 120g")
        );

        assertThat(estimate.calories()).isEqualTo(521);
        assertThat(estimate.proteinG()).isEqualByComparingTo("38.2");
        assertThat(estimate.carbsG()).isEqualByComparingTo("70.0");
        assertThat(estimate.fatG()).isEqualByComparingTo("8.9");
        assertThat(fakeMealNutritionAiClient.receivedPrompt)
                .contains("현미밥 150g, 닭가슴살 120g")
                .contains("계산 기준");
    }

    @Test
    void 음식_입력이_비어_있으면_실패한다() {
        createProfile();

        assertThatThrownBy(() -> mealNutritionEstimateService.estimate(new MealNutritionEstimateCommand(" ")))
                .isInstanceOf(MealNutritionEstimateException.class)
                .hasMessage("먹은 음식과 양을 입력해 주세요.");
    }

    @Test
    void AI_응답이_JSON이_아니면_실패한다() {
        createProfile();
        fakeMealNutritionAiClient.response = "계산하지 못했습니다.";

        assertThatThrownBy(() -> mealNutritionEstimateService.estimate(
                new MealNutritionEstimateCommand("현미밥 150g")
        ))
                .isInstanceOf(MealNutritionEstimateException.class)
                .hasMessage("AI 영양 계산 응답에서 JSON을 찾지 못했습니다.");
    }

    private void createProfile() {
        userProfileService.save(new UserProfileCommand(
                "민수",
                List.of(GoalType.FAT_LOSS),
                new BigDecimal("175.0"),
                new BigDecimal("72.5"),
                ActivityLevel.MODERATE,
                "한식 위주",
                "무릎 통증"
        ));
    }

    @TestConfiguration
    static class FakeMealNutritionAiClientConfig {

        @Bean
        @Primary
        FakeMealNutritionAiClient fakeMealNutritionAiClient() {
            return new FakeMealNutritionAiClient();
        }
    }

    static class FakeMealNutritionAiClient implements MealNutritionAiClient {

        private String response = "{\"calories\": 0, \"protein_g\": 0, \"carbs_g\": 0, \"fat_g\": 0}";
        private String receivedPrompt;

        @Override
        public String estimateNutrition(String prompt) {
            receivedPrompt = prompt;
            return response;
        }

        private void reset() {
            response = "{\"calories\": 0, \"protein_g\": 0, \"carbs_g\": 0, \"fat_g\": 0}";
            receivedPrompt = null;
        }
    }
}
