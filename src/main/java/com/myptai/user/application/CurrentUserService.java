package com.myptai.user.application;

import com.myptai.user.domain.AppUser;
import com.myptai.user.repository.AppUserRepository;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    public CurrentUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findCurrentUser() {
        Optional<String> authenticatedEmail = findAuthenticatedEmail();
        if (authenticatedEmail.isPresent()) {
            return appUserRepository.findByEmail(authenticatedEmail.get());
        }
        return appUserRepository.findFirstByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public AppUser getCurrentUser(Supplier<? extends RuntimeException> exceptionSupplier) {
        return findCurrentUser().orElseThrow(exceptionSupplier);
    }

    private Optional<String> findAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName())
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(value -> !value.isBlank());
    }
}
