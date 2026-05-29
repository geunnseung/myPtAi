package com.myptai.meal.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class MealNutritionEstimateParser {

    private static final BigDecimal MAX_MACRO_GRAMS = new BigDecimal("999.99");
    private static final BigDecimal MAX_CALORIES = new BigDecimal("10000");

    private final ObjectMapper objectMapper;

    public MealNutritionEstimateParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MealNutritionEstimateView parse(String responseText) {
        JsonNode root = readJsonObject(responseText);
        return new MealNutritionEstimateView(
                requiredCalories(root),
                requiredMacro(root, "protein_g"),
                requiredMacro(root, "carbs_g"),
                requiredMacro(root, "fat_g")
        );
    }

    private JsonNode readJsonObject(String responseText) {
        if (responseText == null || responseText.isBlank()) {
            throw new MealNutritionEstimateException("AI 영양 계산 응답이 비어 있습니다.");
        }

        String json = extractJsonObject(responseText);
        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isObject()) {
                throw new MealNutritionEstimateException("AI 영양 계산 응답 형식이 올바르지 않습니다.");
            }
            return root;
        } catch (JsonProcessingException exception) {
            throw new MealNutritionEstimateException("AI 영양 계산 응답을 해석하지 못했습니다.", exception);
        }
    }

    private String extractJsonObject(String responseText) {
        String trimmed = responseText.trim();
        int startIndex = trimmed.indexOf('{');
        int endIndex = trimmed.lastIndexOf('}');
        if (startIndex < 0 || endIndex <= startIndex) {
            throw new MealNutritionEstimateException("AI 영양 계산 응답에서 JSON을 찾지 못했습니다.");
        }
        return trimmed.substring(startIndex, endIndex + 1);
    }

    private Integer requiredCalories(JsonNode root) {
        BigDecimal calories = requiredDecimal(root, "calories");
        if (calories.compareTo(BigDecimal.ZERO) < 0 || calories.compareTo(MAX_CALORIES) > 0) {
            throw new MealNutritionEstimateException("AI가 계산한 칼로리가 허용 범위를 벗어났습니다.");
        }
        return calories.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    private BigDecimal requiredMacro(JsonNode root, String fieldName) {
        BigDecimal grams = requiredDecimal(root, fieldName);
        if (grams.compareTo(BigDecimal.ZERO) < 0 || grams.compareTo(MAX_MACRO_GRAMS) > 0) {
            throw new MealNutritionEstimateException("AI가 계산한 영양성분이 허용 범위를 벗어났습니다.");
        }
        return grams.setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal requiredDecimal(JsonNode root, String fieldName) {
        JsonNode value = root.get(fieldName);
        if (value == null || value.isNull()) {
            throw new MealNutritionEstimateException("AI 영양 계산 응답에 필요한 값이 없습니다: " + fieldName);
        }
        if (value.isNumber()) {
            return value.decimalValue();
        }
        if (value.isTextual() && !value.asText().isBlank()) {
            try {
                return new BigDecimal(value.asText().trim());
            } catch (NumberFormatException exception) {
                throw new MealNutritionEstimateException("AI 영양 계산 응답 값이 숫자가 아닙니다: " + fieldName, exception);
            }
        }
        throw new MealNutritionEstimateException("AI 영양 계산 응답 값이 숫자가 아닙니다: " + fieldName);
    }
}
