package com.myptai.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import com.myptai.user.repository.AppUserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserProfileServiceTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void 프로필이_없으면_새로_생성한다() {
        UserProfileCommand command = new UserProfileCommand(
                "민수",
                List.of(GoalType.FAT_LOSS, GoalType.HEALTH),
                new BigDecimal("175.5"),
                new BigDecimal("72.5"),
                ActivityLevel.MODERATE,
                "한식 위주",
                "무릎 통증"
        );

        UserProfileView savedProfile = userProfileService.save(command);

        assertThat(savedProfile.id()).isNotNull();
        assertThat(savedProfile.displayName()).isEqualTo("민수");
        assertThat(savedProfile.goals()).containsExactly(GoalType.FAT_LOSS, GoalType.HEALTH);
        assertThat(savedProfile.heightCm()).isEqualByComparingTo("175.5");
        assertThat(savedProfile.weightKg()).isEqualByComparingTo("72.5");
        assertThat(appUserRepository.count()).isEqualTo(1);
    }

    @Test
    void 기존_프로필이_있으면_새로_만들지_않고_수정한다() {
        userProfileService.save(new UserProfileCommand(
                "민수",
                List.of(GoalType.FAT_LOSS),
                new BigDecimal("175.0"),
                new BigDecimal("72.5"),
                ActivityLevel.MODERATE,
                "한식 위주",
                "무릎 통증"
        ));

        UserProfileView updatedProfile = userProfileService.save(new UserProfileCommand(
                "민수",
                List.of(GoalType.MUSCLE_GAIN, GoalType.MAINTENANCE),
                new BigDecimal("176.5"),
                new BigDecimal("73.2"),
                ActivityLevel.HIGH,
                "단백질 충분히",
                "없음"
        ));

        assertThat(updatedProfile.goals()).containsExactly(GoalType.MUSCLE_GAIN, GoalType.MAINTENANCE);
        assertThat(updatedProfile.heightCm()).isEqualByComparingTo("176.5");
        assertThat(updatedProfile.weightKg()).isEqualByComparingTo("73.2");
        assertThat(appUserRepository.count()).isEqualTo(1);
    }
}
