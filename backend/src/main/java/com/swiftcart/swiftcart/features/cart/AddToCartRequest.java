package com.swiftcart.swiftcart.features.cart;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddToCartRequest {

    private Long productId;
    private int quantity = 1;
}
