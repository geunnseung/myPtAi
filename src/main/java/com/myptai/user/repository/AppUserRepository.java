package com.myptai.user.repository;

import com.myptai.user.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findFirstByOrderByIdAsc();

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
