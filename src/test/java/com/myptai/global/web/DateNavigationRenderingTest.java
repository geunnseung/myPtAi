package com.myptai.global.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.user.repository.AppUserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
class DateNavigationRenderingTest {

    private static final String USER_EMAIL = "date-navigation@example.com";

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
    @MethodSource("datedPages")
    void 날짜_기반_화면은_이전날과_다음날_이동_링크를_보여준다(
            String path,
            String previousHref,
            String todayHref,
            String nextHref
    ) throws Exception {
        String html = mockMvc.perform(get(path).param("date", "2026-06-22"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("이전날")
                .contains("오늘")
                .contains("다음날")
                .contains(previousHref)
                .contains(todayHref)
                .contains(nextHref);
    }

    private static Stream<Arguments> datedPages() {
        return Stream.of(
                arguments(
                        "/meals",
                        "href=\"/meals?date=2026-06-21\"",
                        "href=\"/meals?date=2026-06-24\"",
                        "href=\"/meals?date=2026-06-23\""
                ),
                arguments(
                        "/workouts",
                        "href=\"/workouts?date=2026-06-21\"",
                        "href=\"/workouts?date=2026-06-24\"",
                        "href=\"/workouts?date=2026-06-23\""
                ),
                arguments(
                        "/conditions",
                        "href=\"/conditions?date=2026-06-21\"",
                        "href=\"/conditions?date=2026-06-24\"",
                        "href=\"/conditions?date=2026-06-23\""
                ),
                arguments(
                        "/coach",
                        "href=\"/coach?date=2026-06-21\"",
                        "href=\"/coach?date=2026-06-24\"",
                        "href=\"/coach?date=2026-06-23\""
                )
        );
    }

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        }
    }
}
