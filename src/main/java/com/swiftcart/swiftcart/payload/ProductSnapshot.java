package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductSnapshot {

    private Long productId;
    private String productName;
    private double price;
    private String image;
}
