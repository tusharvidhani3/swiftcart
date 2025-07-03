package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private SellerProductResponse sellerProduct;
    private int quantity;
}