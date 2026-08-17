package com.swiftcart.swiftcart.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.auth.provider.google")
public record GoogleAuthProperties(String clientId) {}