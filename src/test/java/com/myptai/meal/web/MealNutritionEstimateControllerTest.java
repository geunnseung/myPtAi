package com.myptai.meal.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myptai.meal.application.MealNutritionAiClient;
import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MealNutritionEstimateControllerTest {

    private static final String USER_EMAIL = "meal-estimate@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void setUp() {
        appUserRepository.save(AppUser.signup(
                USER_EMAIL,
                "password-hash",
                "테스터",
                GoalType.FAT_LOSS
        ));
    }

    @WithMockUser(username = USER_EMAIL)
    @Test
    void AI_영양_계산_결과를_식단_입력폼에_반영한다() throws Exception {
        String html = mockMvc.perform(post("/meals/nutrition-estimate")
                        .with(csrf())
                        .param("recordedOn", "2026-06-22")
                        .param("mealType", "BREAKFAST")
                        .param("nutritionEstimateInput", "현미밥 150g, 닭가슴살 120g"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("AI 계산 결과를 입력칸에 반영했습니다.")
                .contains("value=\"521\"")
                .contains("value=\"38.2\"")
                .contains("value=\"70.0\"")
                .contains("value=\"8.9\"");
    }

    @TestConfiguration
    static class FakeMealNutritionAiClientConfig {

        @Bean
        @Primary
        MealNutritionAiClient mealNutritionAiClient() {
            return prompt -> "{\"calories\":521,\"protein_g\":38.2,\"carbs_g\":70.0,\"fat_g\":8.9}";
        }
    }
}
