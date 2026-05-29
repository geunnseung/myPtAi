package com.myptai.meal.application;

import com.myptai.user.application.CurrentUserService;
import org.springframework.stereotype.Service;

@Service
public class MealNutritionEstimateService {

    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    private final CurrentUserService currentUserService;
    private final MealNutritionEstimatePromptBuilder promptBuilder;
    private final MealNutritionAiClient mealNutritionAiClient;
    private final MealNutritionEstimateParser estimateParser;

    public MealNutritionEstimateService(
            CurrentUserService currentUserService,
            MealNutritionEstimatePromptBuilder promptBuilder,
            MealNutritionAiClient mealNutritionAiClient,
            MealNutritionEstimateParser estimateParser
    ) {
        this.currentUserService = currentUserService;
        this.promptBuilder = promptBuilder;
        this.mealNutritionAiClient = mealNutritionAiClient;
        this.estimateParser = estimateParser;
    }

    public MealNutritionEstimateView estimate(MealNutritionEstimateCommand command) {
        currentUserService.getCurrentUser(RequiredUserProfileException::new);
        validate(command);

        String prompt = promptBuilder.build(command);
        String response = mealNutritionAiClient.estimateNutrition(prompt);
        return estimateParser.parse(response);
    }

    private void validate(MealNutritionEstimateCommand command) {
        if (command.description() == null || command.description().isBlank()) {
            throw new MealNutritionEstimateException("먹은 음식과 양을 입력해 주세요.");
        }
        if (command.description().length() > MAX_DESCRIPTION_LENGTH) {
            throw new MealNutritionEstimateException("AI 영양 계산 입력은 1000자 이하로 입력해 주세요.");
        }
    }
}
