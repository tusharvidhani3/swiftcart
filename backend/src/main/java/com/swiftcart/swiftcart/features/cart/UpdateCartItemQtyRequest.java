package com.swiftcart.swiftcart.features.cart;

import jakarta.validation.constraints.Min;

public record UpdateCartItemQtyRequest(

    @Min(1)
    int quantity
) {}