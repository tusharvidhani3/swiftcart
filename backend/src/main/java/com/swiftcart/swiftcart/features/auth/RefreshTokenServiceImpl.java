package com.swiftcart.swiftcart.features.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;
import com.swiftcart.swiftcart.features.user.User;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    RefreshTokenRepo refreshTokenRepo;

    @Autowired
    JwtService jwtService;

    @Override
    public String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        refreshToken.setToken(generateTokenString());
        refreshTokenRepo.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    public String generateAccessToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token);
        User user = refreshToken.getUser();
        UserDetails userDetails = new UserDetailsImpl(user);
        return jwtService.generateToken(userDetails);
    }

    @Override
    public void invalidateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token);
        if(!refreshToken.isRevoked()) {
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
        }
    }

    @Override
    public boolean isValid(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token);
        Instant currentTime = Instant.now();
        if(refreshToken.isRevoked() || currentTime.isAfter(refreshToken.getExpiresAt()))
        return false;
        return true;
    }

    @Override
    @Transactional
    public String rotateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token);
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
        return generateRefreshToken(refreshToken.getUser());
    }

    private String generateTokenString() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Scheduled(cron = "0 0 3 * * ?") // Every day at 3 AM
    public void purgeExpiredTokens() {
        refreshTokenRepo.deleteAllByExpiresAtBefore(Instant.now());
    }

}
