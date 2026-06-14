package com.swiftcart.swiftcart.features.auth;

import java.time.Instant;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByToken(String token);

    @Modifying
    void deleteByExpiresAtBefore(Instant now);

    @EntityGraph(attributePaths = {"user", "role"})
    RefreshToken findByTokenWithUserAndRole(@Param("token") String token);

}
