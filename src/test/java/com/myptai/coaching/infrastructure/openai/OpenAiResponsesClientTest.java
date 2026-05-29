package com.myptai.coaching.infrastructure.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.myptai.coaching.application.OpenAiClientException;
import com.myptai.meal.application.MealNutritionEstimateException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OpenAiResponsesClientTest {

    @Test
    void output_text_필드에서_코칭_응답을_추출한다() {
        OpenAiTestSupport support = createClient("test-api-key");
        support.server.expect(once(), requestTo("https://api.openai.test/responses"))
                .andExpect(method(POST))
                .andExpect(header(AUTHORIZATION, "Bearer test-api-key"))
                .andRespond(withSuccess("{\"output_text\":\"오늘은 단백질을 충분히 챙겨 주세요.\"}", MediaType.APPLICATION_JSON));

        String answer = support.client.createCoachingAnswer("오늘 계획을 알려줘");

        assertThat(answer).isEqualTo("오늘은 단백질을 충분히 챙겨 주세요.");
        support.server.verify();
    }

    @Test
    void output_배열에서_코칭_응답을_추출한다() {
        OpenAiTestSupport support = createClient("test-api-key");
        support.server.expect(once(), requestTo("https://api.openai.test/responses"))
                .andExpect(method(POST))
                .andRespond(withSuccess("""
                        {
                          "output": [
                            {
                              "content": [
                                {
                                  "type": "output_text",
                                  "text": "가벼운 유산소와 충분한 수분 섭취를 권장합니다."
                                }
                              ]
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        String answer = support.client.createCoachingAnswer("오늘 계획을 알려줘");

        assertThat(answer).isEqualTo("가벼운 유산소와 충분한 수분 섭취를 권장합니다.");
        support.server.verify();
    }

    @Test
    void API_키가_없으면_요청하지_않고_실패한다() {
        OpenAiTestSupport support = createClient("");

        assertThatThrownBy(() -> support.client.createCoachingAnswer("오늘 계획을 알려줘"))
                .isInstanceOf(OpenAiClientException.class)
                .hasMessage("OPENAI_API_KEY가 설정되지 않았습니다.");
        support.server.verify();
    }

    @Test
    void output_text_필드에서_영양_계산_응답을_추출한다() {
        OpenAiTestSupport support = createClient("test-api-key");
        support.server.expect(once(), requestTo("https://api.openai.test/responses"))
                .andExpect(method(POST))
                .andExpect(header(AUTHORIZATION, "Bearer test-api-key"))
                .andRespond(withSuccess(
                        "{\"output_text\":\"{\\\"calories\\\":521,\\\"protein_g\\\":38.2,\\\"carbs_g\\\":70.0,\\\"fat_g\\\":8.9}\"}",
                        MediaType.APPLICATION_JSON
                ));

        String answer = support.client.estimateNutrition("현미밥 150g, 닭가슴살 120g");

        assertThat(answer).isEqualTo("{\"calories\":521,\"protein_g\":38.2,\"carbs_g\":70.0,\"fat_g\":8.9}");
        support.server.verify();
    }

    @Test
    void 영양_계산_API_키가_없으면_요청하지_않고_실패한다() {
        OpenAiTestSupport support = createClient("");

        assertThatThrownBy(() -> support.client.estimateNutrition("현미밥 150g"))
                .isInstanceOf(MealNutritionEstimateException.class)
                .hasMessage("OPENAI_API_KEY가 설정되지 않았습니다.");
        support.server.verify();
    }

    @Test
    void 인증_실패_응답은_설정_확인_메시지로_변환한다() {
        OpenAiTestSupport support = createClient("test-api-key");
        support.server.expect(once(), requestTo("https://api.openai.test/responses"))
                .andExpect(method(POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> support.client.createCoachingAnswer("오늘 계획을 알려줘"))
                .isInstanceOf(OpenAiClientException.class)
                .hasMessage("OpenAI API 인증에 실패했습니다. OPENAI_API_KEY 설정을 확인해 주세요.");
        support.server.verify();
    }

    @Test
    void 사용량_제한_응답은_재시도_안내_메시지로_변환한다() {
        OpenAiTestSupport support = createClient("test-api-key");
        support.server.expect(once(), requestTo("https://api.openai.test/responses"))
                .andExpect(method(POST))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(() -> support.client.createCoachingAnswer("오늘 계획을 알려줘"))
                .isInstanceOf(OpenAiClientException.class)
                .hasMessage("OpenAI API 사용량 제한으로 코칭을 생성하지 못했습니다. 잠시 후 다시 시도해 주세요.");
        support.server.verify();
    }

    @Test
    void 응답에서_텍스트를_찾지_못하면_실패한다() {
        OpenAiTestSupport support = createClient("test-api-key");
        support.server.expect(once(), requestTo("https://api.openai.test/responses"))
                .andExpect(method(POST))
                .andRespond(withSuccess("{\"output\":[]}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> support.client.createCoachingAnswer("오늘 계획을 알려줘"))
                .isInstanceOf(OpenAiClientException.class)
                .hasMessage("OpenAI API 응답에서 텍스트를 찾지 못했습니다.");
        support.server.verify();
    }

    private OpenAiTestSupport createClient(String apiKey) {
        OpenAiProperties properties = new OpenAiProperties();
        properties.setBaseUrl("https://api.openai.test");
        properties.setModel("test-model");
        properties.setApiKey(apiKey);

        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl(properties.getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        OpenAiResponsesClient client = new OpenAiResponsesClient(restClientBuilder.build(), properties);
        return new OpenAiTestSupport(client, server);
    }

    private record OpenAiTestSupport(
            OpenAiResponsesClient client,
            MockRestServiceServer server
    ) {
    }
}
