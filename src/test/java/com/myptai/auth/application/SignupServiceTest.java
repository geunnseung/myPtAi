package com.myptai.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.user.domain.UserRole;
import com.myptai.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SignupServiceTest {

    @Autowired
    private SignupService signupService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입을_하면_이메일과_비밀번호_해시를_저장한다() {
        signupService.signup(new SignupCommand(
                "MINSU@example.com",
                "password123",
                "민수",
                GoalType.FAT_LOSS
        ));

        AppUser user = appUserRepository.findByEmail("minsu@example.com").orElseThrow();

        assertThat(user.getDisplayName()).isEqualTo("민수");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getPasswordHash()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", user.getPasswordHash())).isTrue();
    }

    @Test
    void 같은_이메일로_중복_가입할_수_없다() {
        SignupCommand command = new SignupCommand(
                "minsu@example.com",
                "password123",
                "민수",
                GoalType.FAT_LOSS
        );
        signupService.signup(command);

        assertThatThrownBy(() -> signupService.signup(command))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 가입된 이메일입니다: minsu@example.com");
    }
}
