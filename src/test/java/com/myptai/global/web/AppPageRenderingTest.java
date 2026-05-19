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
class AppPageRenderingTest {

    private static final String USER_EMAIL = "viewer@example.com";

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
    @MethodSource("appPages")
    void 주요_앱_화면은_공통_레이아웃으로_렌더링된다(String path, String title, String activeMenuLabel) throws Exception {
        String html = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html).contains("<title>" + title + "</title>");
        assertThat(html).contains("aria-current=\"page\">" + activeMenuLabel + "</a>");
    }

    private static Stream<Arguments> appPages() {
        return Stream.of(
                arguments("/", "홈 | My PT AI", "홈"),
                arguments("/profile", "프로필 | My PT AI", "프로필"),
                arguments("/meals", "식단 기록 | My PT AI", "식단"),
                arguments("/workouts", "운동 기록 | My PT AI", "운동"),
                arguments("/conditions", "컨디션 기록 | My PT AI", "컨디션"),
                arguments("/coach", "AI 코칭 | My PT AI", "AI 코칭")
        );
    }
}
