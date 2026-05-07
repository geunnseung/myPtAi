package com.myptai.user.application;

import com.myptai.user.domain.AppUser;
import com.myptai.user.repository.AppUserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final AppUserRepository appUserRepository;

    public UserProfileService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserProfileView> findCurrentProfile() {
        return appUserRepository.findFirstByOrderByIdAsc()
                .map(UserProfileView::from);
    }

    @Transactional
    public UserProfileView save(UserProfileCommand command) {
        Optional<AppUser> existingUser = appUserRepository.findFirstByOrderByIdAsc();

        AppUser user = existingUser.orElseGet(() -> AppUser.create(
                command.displayName(),
                command.goal(),
                command.heightCm(),
                command.weightKg(),
                command.activityLevel(),
                command.foodPreference(),
                command.restrictions()
        ));

        existingUser.ifPresent(value -> value.updateProfile(
                command.displayName(),
                command.goal(),
                command.heightCm(),
                command.weightKg(),
                command.activityLevel(),
                command.foodPreference(),
                command.restrictions()
        ));

        return UserProfileView.from(appUserRepository.save(user));
    }
}
