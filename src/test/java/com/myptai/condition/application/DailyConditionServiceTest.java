package com.myptai.condition.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myptai.condition.domain.EnergyLevel;
import com.myptai.condition.repository.DailyConditionRepository;
import com.myptai.user.application.UserProfileCommand;
import com.myptai.user.application.UserProfileService;
import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DailyConditionServiceTest {

    @Autowired
    private DailyConditionService dailyConditionService;

    @Autowired
    private DailyConditionRepository dailyConditionRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Test
    void 프로필이_없으면_컨디션_기록을_저장할_수_없다() {
        DailyConditionCommand command = conditionCommand(LocalDate.of(2026, 6, 22), 420, "72.5");

        assertThatThrownBy(() -> dailyConditionService.save(command))
                .isInstanceOf(RequiredUserProfileException.class)
                .hasMessage("컨디션 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }

    @Test
    void 컨디션_기록을_저장하고_날짜별로_조회한다() {
        createProfile();
        LocalDate recordedOn = LocalDate.of(2026, 6, 22);

        dailyConditionService.save(conditionCommand(recordedOn, 420, "72.5"));

        DailyConditionView condition = dailyConditionService.findByDate(recordedOn).orElseThrow();

        assertThat(condition.recordedOn()).isEqualTo(recordedOn);
        assertThat(condition.sleepMinutes()).isEqualTo(420);
        assertThat(condition.bodyWeightKg()).isEqualByComparingTo("72.5");
        assertThat(condition.energyLevel()).isEqualTo(EnergyLevel.NORMAL);
    }

    @Test
    void 같은_날짜에_다시_저장하면_새로_만들지_않고_수정한다() {
        createProfile();
        LocalDate recordedOn = LocalDate.of(2026, 6, 22);
        dailyConditionService.save(conditionCommand(recordedOn, 420, "72.5"));

        DailyConditionView updated = dailyConditionService.save(new DailyConditionCommand(
                recordedOn,
                360,
                new BigDecimal("71.9"),
                EnergyLevel.TIRED,
                "피곤함"
        ));

        assertThat(updated.sleepMinutes()).isEqualTo(360);
        assertThat(updated.bodyWeightKg()).isEqualByComparingTo("71.9");
        assertThat(updated.energyLevel()).isEqualTo(EnergyLevel.TIRED);
        assertThat(dailyConditionRepository.count()).isEqualTo(1);
    }

    @Test
    void 컨디션_기록을_삭제한다() {
        createProfile();
        LocalDate recordedOn = LocalDate.of(2026, 6, 22);
        DailyConditionView saved = dailyConditionService.save(conditionCommand(recordedOn, 420, "72.5"));

        dailyConditionService.delete(recordedOn);

        assertThat(dailyConditionRepository.findById(saved.id())).isEmpty();
    }

    private void createProfile() {
        userProfileService.save(new UserProfileCommand(
                "민수",
                List.of(GoalType.FAT_LOSS),
                new BigDecimal("175.0"),
                new BigDecimal("72.5"),
                ActivityLevel.MODERATE,
                "한식 위주",
                "무릎 통증"
        ));
    }

    private DailyConditionCommand conditionCommand(LocalDate recordedOn, int sleepMinutes, String bodyWeightKg) {
        return new DailyConditionCommand(
                recordedOn,
                sleepMinutes,
                new BigDecimal(bodyWeightKg),
                EnergyLevel.NORMAL,
                "특이사항 없음"
        );
    }
}
