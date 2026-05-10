package com.myptai.workout.application;

import com.myptai.user.domain.AppUser;
import com.myptai.user.repository.AppUserRepository;
import com.myptai.workout.domain.WorkoutRecord;
import com.myptai.workout.repository.WorkoutRecordRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutRecordService {

    private final WorkoutRecordRepository workoutRecordRepository;
    private final AppUserRepository appUserRepository;

    public WorkoutRecordService(WorkoutRecordRepository workoutRecordRepository, AppUserRepository appUserRepository) {
        this.workoutRecordRepository = workoutRecordRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkoutRecordView> findByDate(LocalDate recordedOn) {
        return appUserRepository.findFirstByOrderByIdAsc()
                .map(user -> workoutRecordRepository.findByUser_IdAndRecordedOnOrderByIdDesc(user.getId(), recordedOn)
                        .stream()
                        .map(WorkoutRecordView::from)
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public WorkoutRecordView getForEdit(Long workoutRecordId) {
        AppUser user = getCurrentUser();
        return workoutRecordRepository.findByIdAndUser_Id(workoutRecordId, user.getId())
                .map(WorkoutRecordView::from)
                .orElseThrow(() -> new WorkoutRecordNotFoundException(workoutRecordId));
    }

    @Transactional
    public WorkoutRecordView create(WorkoutRecordCommand command) {
        AppUser user = getCurrentUser();
        WorkoutRecord workoutRecord = WorkoutRecord.create(user, command.toValues());
        return WorkoutRecordView.from(workoutRecordRepository.save(workoutRecord));
    }

    @Transactional
    public WorkoutRecordView update(Long workoutRecordId, WorkoutRecordCommand command) {
        AppUser user = getCurrentUser();
        WorkoutRecord workoutRecord = workoutRecordRepository.findByIdAndUser_Id(workoutRecordId, user.getId())
                .orElseThrow(() -> new WorkoutRecordNotFoundException(workoutRecordId));

        workoutRecord.update(command.toValues());
        return WorkoutRecordView.from(workoutRecord);
    }

    @Transactional
    public void delete(Long workoutRecordId) {
        AppUser user = getCurrentUser();
        WorkoutRecord workoutRecord = workoutRecordRepository.findByIdAndUser_Id(workoutRecordId, user.getId())
                .orElseThrow(() -> new WorkoutRecordNotFoundException(workoutRecordId));

        workoutRecordRepository.delete(workoutRecord);
    }

    private AppUser getCurrentUser() {
        return appUserRepository.findFirstByOrderByIdAsc()
                .orElseThrow(RequiredUserProfileException::new);
    }
}
