package com.swiftcart.swiftcart.features.payment;

public record PaymentDto(
    String paymentOrderId,
    String paymentId,
    PaymentStatus paymentStatus
) {}