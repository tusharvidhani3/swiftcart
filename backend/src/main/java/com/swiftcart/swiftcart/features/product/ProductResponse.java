package com.swiftcart.swiftcart.features.product;

import java.util.List;

import jakarta.validation.constraints.DecimalMin;

public record ProductResponse(
    Long id,
    String name,

    @DecimalMin("1.0")
    double price,

    @DecimalMin("1.0")
    double mrp,

    List<String> imageUrls,
    String category,
    String description,
    String stock
) {}