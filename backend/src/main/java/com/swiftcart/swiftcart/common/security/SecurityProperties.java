package com.swiftcart.swiftcart.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
    @Name("auth") AuthProperties auth,
    @Name("cors") CorsProperties cors
) {
    public record AuthProperties(
        @Name("access-token") AccessTokenProperties accessToken,
        @Name("refresh-token") RefreshTokenProperties refreshToken
    ) {
        public record AccessTokenProperties(String secretKey, long expirationMinutes) {}
        public record RefreshTokenProperties(long expirationDays) {}
    }

    public record CorsProperties(String[] allowedOrigins) {}
}