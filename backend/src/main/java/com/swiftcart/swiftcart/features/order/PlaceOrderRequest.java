package com.swiftcart.swiftcart.features.order;

public record PlaceOrderRequest(
    Boolean prepaid,
    Long shippingAddressId
) {}