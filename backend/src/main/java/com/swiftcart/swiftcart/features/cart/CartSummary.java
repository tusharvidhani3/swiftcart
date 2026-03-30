package com.swiftcart.swiftcart.features.cart;

import java.util.List;

public record CartSummary(
    Long id,
    List<CartItemResponse> items,
    long subtotal,
    long shippingCharge,
    long totalAmount
) {}