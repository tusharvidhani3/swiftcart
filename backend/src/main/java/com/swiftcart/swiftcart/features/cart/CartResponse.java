package com.swiftcart.swiftcart.features.cart;

import java.util.List;

public record CartResponse(
    Long id,
    List<CartItemResponse> items,
    double totalPrice
) {}
