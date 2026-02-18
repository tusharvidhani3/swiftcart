package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.features.product.ProductResponse;

public record OrderItemResponse(
    Long id,
    LocalDateTime deliveryAt,
    ProductResponse product,
    OrderStatus orderItemStatus,
    int quantity
) {}
