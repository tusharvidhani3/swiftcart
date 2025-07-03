package com.swiftcart.swiftcart.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.RefreshToken;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    public RefreshToken findByToken(String token);

    @Modifying
    public void deleteAllByExpiresAtBefore(Instant now);

}
