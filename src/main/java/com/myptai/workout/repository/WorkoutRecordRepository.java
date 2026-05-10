package com.myptai.workout.repository;

import com.myptai.workout.domain.WorkoutRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {

    List<WorkoutRecord> findByUser_IdAndRecordedOnOrderByIdDesc(Long userId, LocalDate recordedOn);

    Optional<WorkoutRecord> findByIdAndUser_Id(Long id, Long userId);
}
