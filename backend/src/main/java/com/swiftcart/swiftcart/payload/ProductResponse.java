package com.swiftcart.swiftcart.payload;

import java.util.List;

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
    @DecimalMin("1.0")
    private double mrp;
    private List<String> imageUrls;
    private String category;
    private String description;
    private String stock;
}
