package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellerProductResponse {

    private Long sellerProductId;

    private ProductResponse product;

    private SellerDTO seller;

    private double price;

    private int stock;

    private boolean isActive;

    // private String sellerSku;
    // private int minimumOrderQuantity;
}
