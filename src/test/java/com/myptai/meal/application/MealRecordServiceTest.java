package com.myptai.meal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myptai.meal.domain.MealType;
import com.myptai.meal.repository.MealRecordRepository;
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
class MealRecordServiceTest {

    @Autowired
    private MealRecordService mealRecordService;

    @Autowired
    private MealRecordRepository mealRecordRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Test
    void 프로필이_없으면_식단_기록을_생성할_수_없다() {
        MealRecordCommand command = mealCommand("현미밥과 닭가슴살", LocalDate.of(2026, 6, 22));

        assertThatThrownBy(() -> mealRecordService.create(command))
                .isInstanceOf(RequiredUserProfileException.class)
                .hasMessage("식단 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }

    @Test
    void 식단_기록을_생성하고_날짜별로_조회한다() {
        createProfile();
        LocalDate recordedOn = LocalDate.of(2026, 6, 22);

        mealRecordService.create(mealCommand("현미밥과 닭가슴살", recordedOn));
        mealRecordService.create(new MealRecordCommand(
                recordedOn.plusDays(1),
                MealType.DINNER,
                "연어 샐러드",
                520,
                new BigDecimal("38.0"),
                new BigDecimal("32.0"),
                new BigDecimal("20.0"),
                "저녁"
        ));

        List<MealRecordView> meals = mealRecordService.findByDate(recordedOn);

        assertThat(meals).hasSize(1);
        assertThat(meals.getFirst().name()).isEqualTo("현미밥과 닭가슴살");
        assertThat(meals.getFirst().proteinG()).isEqualByComparingTo("35.0");
    }

    @Test
    void 식단_기록을_수정한다() {
        createProfile();
        MealRecordView created = mealRecordService.create(
                mealCommand("현미밥과 닭가슴살", LocalDate.of(2026, 6, 22))
        );

        MealRecordView updated = mealRecordService.update(created.id(), new MealRecordCommand(
                LocalDate.of(2026, 6, 23),
                MealType.LUNCH,
                "소고기 비빔밥",
                760,
                new BigDecimal("42.0"),
                new BigDecimal("88.0"),
                new BigDecimal("18.0"),
                "점심 외식"
        ));

        assertThat(updated.recordedOn()).isEqualTo(LocalDate.of(2026, 6, 23));
        assertThat(updated.mealType()).isEqualTo(MealType.LUNCH);
        assertThat(updated.name()).isEqualTo("소고기 비빔밥");
        assertThat(updated.calories()).isEqualTo(760);
    }

    @Test
    void 식단_기록을_삭제한다() {
        createProfile();
        MealRecordView created = mealRecordService.create(
                mealCommand("현미밥과 닭가슴살", LocalDate.of(2026, 6, 22))
        );

        mealRecordService.delete(created.id());

        assertThat(mealRecordRepository.findById(created.id())).isEmpty();
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

    private MealRecordCommand mealCommand(String name, LocalDate recordedOn) {
        return new MealRecordCommand(
                recordedOn,
                MealType.BREAKFAST,
                name,
                650,
                new BigDecimal("35.0"),
                new BigDecimal("72.0"),
                new BigDecimal("16.0"),
                "든든하게 먹음"
        );
    }
}
