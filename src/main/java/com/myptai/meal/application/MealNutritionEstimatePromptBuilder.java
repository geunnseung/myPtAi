package com.myptai.meal.application;

import org.springframework.stereotype.Component;

@Component
public class MealNutritionEstimatePromptBuilder {

    public String build(MealNutritionEstimateCommand command) {
        return """
                [식단 입력]
                %s

                [계산 기준]
                - 사용자가 적은 음식명과 g 단위를 기준으로 총합을 계산한다.
                - 일반적인 조리 후 섭취 중량 기준으로 추정한다.
                - 모호한 음식은 한국에서 흔한 조리 방식과 평균 영양성분을 사용한다.
                - 확실하지 않아도 가장 합리적인 단일 추정값을 낸다.
                """.formatted(command.description().trim());
    }
}
