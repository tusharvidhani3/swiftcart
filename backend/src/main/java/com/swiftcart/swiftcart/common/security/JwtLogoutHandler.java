package com.swiftcart.swiftcart.common.security;

import java.time.Duration;

import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.swiftcart.swiftcart.features.auth.RefreshTokenService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        Cookie accessTokenCookie = WebUtils.getCookie(request, "access_token");

        if (accessTokenCookie != null) {
            String accessToken = accessTokenCookie.getValue();

            Claims claims = jwtService.extractAllClaims(accessToken);
            String jwtId = claims.getId();

            long expirationTimeInMs = claims.getExpiration().getTime();
            long remainingTimeInSeconds = (expirationTimeInMs - System.currentTimeMillis()) / 1000;

            if (remainingTimeInSeconds > 0 && jwtId != null)
                redisTemplate.opsForValue().set("blacklist:" + jwtId, "revoked", Duration.ofSeconds(remainingTimeInSeconds));
        }

        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh_token");
        if(refreshTokenCookie != null) {
            String refreshToken = refreshTokenCookie.getValue();
            refreshTokenService.invalidateRefreshToken(refreshToken);
        }
    }

}
