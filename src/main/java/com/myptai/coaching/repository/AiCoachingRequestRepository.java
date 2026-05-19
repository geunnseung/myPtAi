package com.myptai.coaching.repository;

import com.myptai.coaching.domain.AiCoachingRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiCoachingRequestRepository extends JpaRepository<AiCoachingRequest, Long> {

    List<AiCoachingRequest> findTop10ByUser_IdOrderByCreatedAtDesc(Long userId);

    List<AiCoachingRequest> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<AiCoachingRequest> findByUser_IdAndTargetDateOrderByCreatedAtDesc(Long userId, LocalDate targetDate);

    Optional<AiCoachingRequest> findByIdAndUser_Id(Long id, Long userId);
}
