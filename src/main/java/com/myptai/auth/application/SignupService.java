package com.myptai.auth.application;

import com.myptai.user.domain.AppUser;
import com.myptai.user.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupCommand command) {
        String email = command.normalizedEmail();
        if (appUserRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        AppUser user = AppUser.signup(
                email,
                passwordEncoder.encode(command.password()),
                command.displayName(),
                command.goal()
        );
        appUserRepository.save(user);
    }
}
