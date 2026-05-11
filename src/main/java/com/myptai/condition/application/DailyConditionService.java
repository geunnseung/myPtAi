package com.myptai.condition.application;

import com.myptai.condition.domain.DailyCondition;
import com.myptai.condition.repository.DailyConditionRepository;
import com.myptai.user.domain.AppUser;
import com.myptai.user.repository.AppUserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyConditionService {

    private final DailyConditionRepository dailyConditionRepository;
    private final AppUserRepository appUserRepository;

    public DailyConditionService(
            DailyConditionRepository dailyConditionRepository,
            AppUserRepository appUserRepository
    ) {
        this.dailyConditionRepository = dailyConditionRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public Optional<DailyConditionView> findByDate(LocalDate recordedOn) {
        return appUserRepository.findFirstByOrderByIdAsc()
                .flatMap(user -> dailyConditionRepository.findByUser_IdAndRecordedOn(user.getId(), recordedOn))
                .map(DailyConditionView::from);
    }

    @Transactional
    public DailyConditionView save(DailyConditionCommand command) {
        AppUser user = getCurrentUser();
        Optional<DailyCondition> existingCondition =
                dailyConditionRepository.findByUser_IdAndRecordedOn(user.getId(), command.recordedOn());

        DailyCondition dailyCondition = existingCondition.orElseGet(
                () -> DailyCondition.create(user, command.toValues())
        );

        existingCondition.ifPresent(value -> value.update(command.toValues()));

        return DailyConditionView.from(dailyConditionRepository.save(dailyCondition));
    }

    @Transactional
    public void delete(LocalDate recordedOn) {
        AppUser user = getCurrentUser();
        DailyCondition dailyCondition = dailyConditionRepository.findByUser_IdAndRecordedOn(user.getId(), recordedOn)
                .orElseThrow(() -> new DailyConditionNotFoundException(recordedOn));

        dailyConditionRepository.delete(dailyCondition);
    }

    private AppUser getCurrentUser() {
        return appUserRepository.findFirstByOrderByIdAsc()
                .orElseThrow(RequiredUserProfileException::new);
    }
}
