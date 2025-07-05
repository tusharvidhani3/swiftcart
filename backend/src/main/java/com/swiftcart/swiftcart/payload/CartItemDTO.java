package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private ProductResponse product;
    private int quantity;
}