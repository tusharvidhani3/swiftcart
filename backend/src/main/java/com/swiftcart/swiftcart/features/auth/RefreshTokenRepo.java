package com.swiftcart.swiftcart.features.auth;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    public RefreshToken findByToken(String token);

    @Modifying
    public void deleteAllByExpiresAtBefore(Instant now);

}
