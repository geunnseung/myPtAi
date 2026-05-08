package com.myptai.meal.application;

import com.myptai.meal.domain.MealRecord;
import com.myptai.meal.repository.MealRecordRepository;
import com.myptai.user.domain.AppUser;
import com.myptai.user.repository.AppUserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;
    private final AppUserRepository appUserRepository;

    public MealRecordService(MealRecordRepository mealRecordRepository, AppUserRepository appUserRepository) {
        this.mealRecordRepository = mealRecordRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<MealRecordView> findByDate(LocalDate recordedOn) {
        return appUserRepository.findFirstByOrderByIdAsc()
                .map(user -> mealRecordRepository.findByUser_IdAndRecordedOnOrderByIdDesc(user.getId(), recordedOn)
                        .stream()
                        .map(MealRecordView::from)
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public MealRecordView getForEdit(Long mealRecordId) {
        AppUser user = getCurrentUser();
        return mealRecordRepository.findByIdAndUser_Id(mealRecordId, user.getId())
                .map(MealRecordView::from)
                .orElseThrow(() -> new MealRecordNotFoundException(mealRecordId));
    }

    @Transactional
    public MealRecordView create(MealRecordCommand command) {
        AppUser user = getCurrentUser();
        MealRecord mealRecord = MealRecord.create(user, command.toValues());
        return MealRecordView.from(mealRecordRepository.save(mealRecord));
    }

    @Transactional
    public MealRecordView update(Long mealRecordId, MealRecordCommand command) {
        AppUser user = getCurrentUser();
        MealRecord mealRecord = mealRecordRepository.findByIdAndUser_Id(mealRecordId, user.getId())
                .orElseThrow(() -> new MealRecordNotFoundException(mealRecordId));

        mealRecord.update(command.toValues());
        return MealRecordView.from(mealRecord);
    }

    @Transactional
    public void delete(Long mealRecordId) {
        AppUser user = getCurrentUser();
        MealRecord mealRecord = mealRecordRepository.findByIdAndUser_Id(mealRecordId, user.getId())
                .orElseThrow(() -> new MealRecordNotFoundException(mealRecordId));

        mealRecordRepository.delete(mealRecord);
    }

    private AppUser getCurrentUser() {
        return appUserRepository.findFirstByOrderByIdAsc()
                .orElseThrow(RequiredUserProfileException::new);
    }
}
