package com.myptai.coaching.infrastructure.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.myptai.coaching.application.OpenAiClient;
import com.myptai.coaching.application.OpenAiClientException;
import com.myptai.meal.application.MealNutritionAiClient;
import com.myptai.meal.application.MealNutritionEstimateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class OpenAiResponsesClient implements OpenAiClient, MealNutritionAiClient {

    private static final String COACHING_INSTRUCTIONS = """
            너는 식단과 운동 기록을 바탕으로 현실적인 한국어 코칭을 제공하는 퍼스널 트레이너다.
            사용자의 목표, 최근 기록, 컨디션을 근거로 오늘 실행할 수 있는 식단과 운동 제안을 작성한다.
            의료 진단이나 치료처럼 말하지 말고, 통증이나 질환 위험이 보이면 전문가 상담을 권한다.
            답변은 과장하지 말고 구체적인 행동 단위로 작성한다.
            """;

    private static final String MEAL_NUTRITION_INSTRUCTIONS = """
            너는 음식 섭취량을 바탕으로 영양성분을 추정하는 식단 기록 보조자다.
            사용자가 입력한 음식명과 g 단위를 기준으로 총 칼로리, 단백질, 탄수화물, 지방을 계산한다.
            응답은 설명 없이 JSON 객체만 작성한다.
            JSON 필드는 calories, protein_g, carbs_g, fat_g 네 개만 사용한다.
            calories는 정수 kcal, protein_g/carbs_g/fat_g는 g 단위 숫자로 작성한다.
            """;

    private final RestClient restClient;
    private final OpenAiProperties properties;

    @Autowired
    public OpenAiResponsesClient(RestClient.Builder restClientBuilder, OpenAiProperties properties) {
        this(buildRestClient(restClientBuilder, properties), properties);
    }

    OpenAiResponsesClient(RestClient restClient, OpenAiProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    private static RestClient buildRestClient(RestClient.Builder restClientBuilder, OpenAiProperties properties) {
        return restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .requestFactory(createRequestFactory(properties))
                .build();
    }

    private static ClientHttpRequestFactory createRequestFactory(OpenAiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        return requestFactory;
    }

    @Override
    public String modelName() {
        return properties.getModel();
    }

    @Override
    public String createCoachingAnswer(String prompt) {
        return createResponse(COACHING_INSTRUCTIONS, prompt);
    }

    @Override
    public String estimateNutrition(String prompt) {
        try {
            return createResponse(MEAL_NUTRITION_INSTRUCTIONS, prompt);
        } catch (OpenAiClientException exception) {
            throw new MealNutritionEstimateException(exception.getMessage(), exception);
        }
    }

    private String createResponse(String instructions, String prompt) {
        if (!properties.hasApiKey()) {
            throw new OpenAiClientException("OPENAI_API_KEY가 설정되지 않았습니다.");
        }

        try {
            JsonNode response = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth(properties.getApiKey()))
                    .body(new OpenAiResponseRequest(properties.getModel(), instructions, prompt))
                    .retrieve()
                    .body(JsonNode.class);

            return extractOutputText(response);
        } catch (RestClientResponseException exception) {
            throw new OpenAiClientException(toFailureMessage(exception), exception);
        } catch (ResourceAccessException exception) {
            throw new OpenAiClientException(
                    "OpenAI API에 연결하지 못했습니다. 네트워크 상태를 확인한 뒤 다시 시도해 주세요.",
                    exception
            );
        } catch (RestClientException exception) {
            throw new OpenAiClientException("OpenAI API 요청 중 오류가 발생했습니다.", exception);
        }
    }

    private String toFailureMessage(RestClientResponseException exception) {
        HttpStatusCode statusCode = exception.getStatusCode();
        int statusValue = statusCode.value();

        if (statusValue == 400) {
            return "OpenAI API 요청 형식이 올바르지 않습니다. 모델 설정을 확인해 주세요.";
        }
        if (statusValue == 401 || statusValue == 403) {
            return "OpenAI API 인증에 실패했습니다. OPENAI_API_KEY 설정을 확인해 주세요.";
        }
        if (statusValue == 404) {
            return "OpenAI API 엔드포인트를 찾지 못했습니다. base-url 설정을 확인해 주세요.";
        }
        if (statusValue == 429) {
            return "OpenAI API 사용량 제한으로 코칭을 생성하지 못했습니다. 잠시 후 다시 시도해 주세요.";
        }
        if (statusCode.is5xxServerError()) {
            return "OpenAI API 서버 오류로 코칭을 생성하지 못했습니다. 잠시 후 다시 시도해 주세요.";
        }
        return "OpenAI API 요청이 실패했습니다. 상태 코드: " + statusValue;
    }

    private String extractOutputText(JsonNode response) {
        if (response == null) {
            throw new OpenAiClientException("OpenAI API 응답이 비어 있습니다.");
        }

        JsonNode outputText = response.get("output_text");
        if (outputText != null && !outputText.asText().isBlank()) {
            return outputText.asText();
        }

        JsonNode output = response.get("output");
        if (output != null && output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.get("content");
                if (content == null || !content.isArray()) {
                    continue;
                }
                for (JsonNode contentItem : content) {
                    JsonNode text = contentItem.get("text");
                    if (text != null && !text.asText().isBlank()) {
                        return text.asText();
                    }
                }
            }
        }

        throw new OpenAiClientException("OpenAI API 응답에서 텍스트를 찾지 못했습니다.");
    }

    private record OpenAiResponseRequest(
            String model,
            String instructions,
            String input
    ) {
    }
}
