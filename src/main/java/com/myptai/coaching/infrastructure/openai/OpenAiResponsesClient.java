package com.myptai.coaching.infrastructure.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.myptai.coaching.application.OpenAiClient;
import com.myptai.coaching.application.OpenAiClientException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class OpenAiResponsesClient implements OpenAiClient {

    private static final String INSTRUCTIONS = """
            너는 식단과 운동 기록을 바탕으로 현실적인 한국어 코칭을 제공하는 퍼스널 트레이너다.
            사용자의 목표, 최근 기록, 컨디션을 근거로 오늘 실행할 수 있는 식단과 운동 제안을 작성한다.
            의료 진단이나 치료처럼 말하지 말고, 통증이나 질환 위험이 보이면 전문가 상담을 권한다.
            답변은 과장하지 말고 구체적인 행동 단위로 작성한다.
            """;

    private final RestClient restClient;
    private final OpenAiProperties properties;

    public OpenAiResponsesClient(RestClient.Builder restClientBuilder, OpenAiProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .build();
        this.properties = properties;
    }

    @Override
    public String modelName() {
        return properties.getModel();
    }

    @Override
    public String createCoachingAnswer(String prompt) {
        if (!properties.hasApiKey()) {
            throw new OpenAiClientException("OPENAI_API_KEY가 설정되지 않았습니다.");
        }

        try {
            JsonNode response = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth(properties.getApiKey()))
                    .body(new OpenAiResponseRequest(properties.getModel(), INSTRUCTIONS, prompt))
                    .retrieve()
                    .body(JsonNode.class);

            return extractOutputText(response);
        } catch (RestClientResponseException exception) {
            throw new OpenAiClientException(
                    "OpenAI API 요청이 실패했습니다. 상태 코드: " + exception.getStatusCode().value(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new OpenAiClientException("OpenAI API 요청 중 네트워크 오류가 발생했습니다.", exception);
        }
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
