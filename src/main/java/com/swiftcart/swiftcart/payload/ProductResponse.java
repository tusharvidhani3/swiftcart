package com.swiftcart.swiftcart.payload;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponse {

    private Long productId;
    private String productName;
    @DecimalMin("1.0")
    private double price;
    private String image;
    private String category;
    private String description;
    private String stock;
}
