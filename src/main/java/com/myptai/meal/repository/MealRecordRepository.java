package com.myptai.meal.repository;

import com.myptai.meal.domain.MealRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {

    List<MealRecord> findByUser_IdAndRecordedOnOrderByIdDesc(Long userId, LocalDate recordedOn);

    List<MealRecord> findByUser_IdAndRecordedOnBetweenOrderByRecordedOnDescIdDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<MealRecord> findByIdAndUser_Id(Long id, Long userId);
}
