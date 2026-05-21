package com.myptai.global.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.myptai.condition.application.DailyConditionCommand;
import com.myptai.condition.application.DailyConditionService;
import com.myptai.condition.application.DailyConditionView;
import com.myptai.condition.domain.EnergyLevel;
import com.myptai.meal.application.MealRecordCommand;
import com.myptai.meal.application.MealRecordService;
import com.myptai.meal.application.MealRecordView;
import com.myptai.meal.domain.MealType;
import com.myptai.user.domain.AppUser;
import com.myptai.user.domain.GoalType;
import com.myptai.user.repository.AppUserRepository;
import com.myptai.workout.application.WorkoutRecordCommand;
import com.myptai.workout.application.WorkoutRecordService;
import com.myptai.workout.application.WorkoutRecordView;
import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutType;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeleteConfirmationPageTest {

    private static final String USER_EMAIL = "delete-confirm@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MealRecordService mealRecordService;

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @Autowired
    private DailyConditionService dailyConditionService;

    @BeforeEach
    void setUp() {
        appUserRepository.save(AppUser.signup(
                USER_EMAIL,
                "password-hash",
                "테스터",
                GoalType.FAT_LOSS
        ));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void 식단_삭제_확인_화면을_렌더링한다() throws Exception {
        MealRecordView mealRecord = mealRecordService.create(new MealRecordCommand(
                LocalDate.of(2026, 6, 22),
                MealType.BREAKFAST,
                "현미밥과 닭가슴살",
                650,
                new BigDecimal("35.0"),
                new BigDecimal("72.0"),
                new BigDecimal("16.0"),
                "든든하게 먹음"
        ));

        String html = mockMvc.perform(get("/meals/{id}/delete", mealRecord.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("<title>식단 삭제 | My PT AI</title>")
                .contains("현미밥과 닭가슴살")
                .contains("삭제 확정");
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void 운동_삭제_확인_화면을_렌더링한다() throws Exception {
        WorkoutRecordView workoutRecord = workoutRecordService.create(new WorkoutRecordCommand(
                LocalDate.of(2026, 6, 22),
                WorkoutType.STRENGTH,
                "전신 근력 운동",
                60,
                WorkoutIntensity.NORMAL,
                "스쿼트는 가볍게 진행"
        ));

        String html = mockMvc.perform(get("/workouts/{id}/delete", workoutRecord.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("<title>운동 삭제 | My PT AI</title>")
                .contains("전신 근력 운동")
                .contains("삭제 확정");
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void 컨디션_삭제_확인_화면을_렌더링한다() throws Exception {
        DailyConditionView condition = dailyConditionService.save(new DailyConditionCommand(
                LocalDate.of(2026, 6, 22),
                420,
                new BigDecimal("72.5"),
                EnergyLevel.NORMAL,
                "특이사항 없음"
        ));

        String html = mockMvc.perform(get("/conditions/delete")
                        .param("date", condition.recordedOn().toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("<title>컨디션 삭제 | My PT AI</title>")
                .contains("하루 컨디션")
                .contains("삭제 확정");
    }
}
