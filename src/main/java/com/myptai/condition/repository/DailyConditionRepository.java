package com.myptai.condition.repository;

import com.myptai.condition.domain.DailyCondition;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyConditionRepository extends JpaRepository<DailyCondition, Long> {

    Optional<DailyCondition> findByUser_IdAndRecordedOn(Long userId, LocalDate recordedOn);
}
