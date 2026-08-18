package com.swiftcart.swiftcart.features.product;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.product")
public record ProductProperties(
    String imagesDir
) {}