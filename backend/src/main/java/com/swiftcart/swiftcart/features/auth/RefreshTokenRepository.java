package com.swiftcart.swiftcart.features.auth;

import java.time.Instant;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @EntityGraph(attributePaths = {"user", "user.role"})
    RefreshToken findByToken(String token);

    @Modifying
    void deleteByExpiresAtBefore(Instant now);

}
