package com.swiftcart.swiftcart.features.product;

import java.util.List;

import jakarta.validation.constraints.Min;

public record SellerProductResponse(
    Long id,
    String name,

    @Min(100)
    long price,

    @Min(100)
    long mrp,

    List<String> imageUrls,
    String category,
    String description,
    Integer stock
) {}
