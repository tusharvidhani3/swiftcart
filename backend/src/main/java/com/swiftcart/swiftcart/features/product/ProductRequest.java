package com.swiftcart.swiftcart.features.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProductRequest(

    @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
    String name,

    @Min(value = 1, message = "Price of a product cannot be less than 1 rupee")
    Double price,
    
    @Min(value = 1, message = "MRP of a product cannot be less than 1 rupee")
    Double mrp,

    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    String category,

    @Size(min = 10, max = 1000, message = "Description must be between 10 & 1000 characters")
    String description,

    @Min(value = 0, message = "Stock quantity cannot be less than 0")
    Integer stock
) {}