package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private Double price;
    private String image;
    private String category;
}
