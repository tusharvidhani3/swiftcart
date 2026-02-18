package com.swiftcart.swiftcart.features.order;

public record PlaceBuyNowOrderRequest(
    Long cartItemId, Long shippingAddressId,
    Boolean prepaid
) {}