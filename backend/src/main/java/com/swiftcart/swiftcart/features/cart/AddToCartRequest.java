package com.swiftcart.swiftcart.features.cart;

import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
    @NotNull
    Long productId,
    int quantity
) {
    public AddToCartRequest {
        if(quantity <= 0)
            quantity = 1;
    }
}