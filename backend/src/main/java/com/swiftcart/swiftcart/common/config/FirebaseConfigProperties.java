package com.swiftcart.swiftcart.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "app.firebase")
public record FirebaseConfigProperties(Resource configPath) {}