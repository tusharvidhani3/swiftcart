package com.swiftcart.swiftcart.features.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.shipping")
public record ShippingProperties(
    long flatRate,
    long freeThreshold
) {
    public ShippingProperties {
        flatRate = flatRate * 100;
        freeThreshold = freeThreshold * 100;
    }
}