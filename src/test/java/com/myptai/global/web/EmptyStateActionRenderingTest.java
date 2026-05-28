package com.myptai.global.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.user.repository.AppUserRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmptyStateActionRenderingTest {

    private static final String USER_EMAIL = "empty-state@example.com";

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
    @ParameterizedTest
    @MethodSource("emptyStatePages")
    void 기록이_없는_화면은_입력_행동으로_이어지는_링크를_보여준다(
            String path,
            String formId,
            String actionHref,
            String actionLabel
    ) throws Exception {
        String html = mockMvc.perform(get(path).param("date", "2026-06-22"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains(formId)
                .contains(actionHref)
                .contains(actionLabel);
    }

    private static Stream<Arguments> emptyStatePages() {
        return Stream.of(
                arguments("/meals", "id=\"meal-form\"", "href=\"#meal-form\"", "식단 입력하기"),
                arguments("/workouts", "id=\"workout-form\"", "href=\"#workout-form\"", "운동 입력하기"),
                arguments("/conditions", "id=\"condition-form\"", "href=\"#condition-form\"", "컨디션 입력하기"),
                arguments("/coach", "id=\"coaching-form\"", "href=\"#coaching-form\"", "코칭 요청하기"),
                arguments("/coach/history", "href=\"/coach#coaching-form\"", "href=\"/coach#coaching-form\"", "코칭 요청하기")
        );
    }
}
