package com.myptai.global.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginSuccessRedirectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.save(AppUser.signup(
                "dashboard@example.com",
                passwordEncoder.encode("password123"),
                "대시보드테스터",
                GoalType.FAT_LOSS
        ));
    }

    @Test
    void 로그인에_성공하면_홈_대시보드로_이동한다() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "dashboard@example.com")
                        .param("password", "password123"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }
}
