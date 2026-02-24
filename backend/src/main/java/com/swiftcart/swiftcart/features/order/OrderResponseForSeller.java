package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.swiftcart.features.address.AddressSnapshot;
import com.swiftcart.swiftcart.features.payment.PaymentDto;

public record OrderResponseForSeller(
    Long id,
    LocalDateTime placedAt,
    long totalAmount,
    PaymentDto payment,
    String buyerName,
    AddressSnapshot address,
    List<OrderItemResponse> items
) {}
