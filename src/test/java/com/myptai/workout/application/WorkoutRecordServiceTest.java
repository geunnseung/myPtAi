package com.myptai.workout.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.myptai.user.application.UserProfileCommand;
import com.myptai.user.application.UserProfileService;
import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutType;
import com.myptai.workout.repository.WorkoutRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class WorkoutRecordServiceTest {

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @Autowired
    private WorkoutRecordRepository workoutRecordRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Test
    void 프로필이_없으면_운동_기록을_생성할_수_없다() {
        WorkoutRecordCommand command = workoutCommand("하체 루틴 A", LocalDate.of(2026, 6, 22));

        assertThatThrownBy(() -> workoutRecordService.create(command))
                .isInstanceOf(RequiredUserProfileException.class)
                .hasMessage("운동 기록을 사용하려면 프로필을 먼저 등록해야 합니다.");
    }

    @Test
    void 운동_기록을_생성하고_날짜별로_조회한다() {
        createProfile();
        LocalDate recordedOn = LocalDate.of(2026, 6, 22);

        workoutRecordService.create(workoutCommand("하체 루틴 A", recordedOn));
        workoutRecordService.create(new WorkoutRecordCommand(
                recordedOn.plusDays(1),
                WorkoutType.CARDIO,
                "존2 러닝",
                40,
                WorkoutIntensity.NORMAL,
                "호흡 편안함"
        ));

        List<WorkoutRecordView> workouts = workoutRecordService.findByDate(recordedOn);

        assertThat(workouts).hasSize(1);
        assertThat(workouts.getFirst().title()).isEqualTo("하체 루틴 A");
        assertThat(workouts.getFirst().durationMinutes()).isEqualTo(60);
        assertThat(workouts.getFirst().intensity()).isEqualTo(WorkoutIntensity.HARD);
    }

    @Test
    void 운동_기록을_수정한다() {
        createProfile();
        WorkoutRecordView created = workoutRecordService.create(
                workoutCommand("하체 루틴 A", LocalDate.of(2026, 6, 22))
        );

        WorkoutRecordView updated = workoutRecordService.update(created.id(), new WorkoutRecordCommand(
                LocalDate.of(2026, 6, 23),
                WorkoutType.STRENGTH,
                "상체 루틴 B",
                70,
                WorkoutIntensity.VERY_HARD,
                "벤치프레스 5x5"
        ));

        assertThat(updated.recordedOn()).isEqualTo(LocalDate.of(2026, 6, 23));
        assertThat(updated.workoutType()).isEqualTo(WorkoutType.STRENGTH);
        assertThat(updated.title()).isEqualTo("상체 루틴 B");
        assertThat(updated.durationMinutes()).isEqualTo(70);
        assertThat(updated.intensity()).isEqualTo(WorkoutIntensity.VERY_HARD);
    }

    @Test
    void 운동_기록을_삭제한다() {
        createProfile();
        WorkoutRecordView created = workoutRecordService.create(
                workoutCommand("하체 루틴 A", LocalDate.of(2026, 6, 22))
        );

        workoutRecordService.delete(created.id());

        assertThat(workoutRecordRepository.findById(created.id())).isEmpty();
    }

    private void createProfile() {
        userProfileService.save(new UserProfileCommand(
                "민수",
                GoalType.FAT_LOSS,
                175,
                new BigDecimal("72.5"),
                ActivityLevel.MODERATE,
                "한식 위주",
                "무릎 통증"
        ));
    }

    private WorkoutRecordCommand workoutCommand(String title, LocalDate recordedOn) {
        return new WorkoutRecordCommand(
                recordedOn,
                WorkoutType.STRENGTH,
                title,
                60,
                WorkoutIntensity.HARD,
                "스쿼트 5x5, RPE 7"
        );
    }
}
