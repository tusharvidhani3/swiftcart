package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.payment.PaymentDto;

public record OrderResponse(
    Long id,
    AddressSnapshot shippingAddress,
    double totalAmount,
    LocalDateTime placedAt,
    List<OrderItemResponse> items,
    PaymentDto payment
) {}
