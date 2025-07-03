package com.swiftcart.swiftcart.payload;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSellerProductRequest {



    @Min(value = 1, message = "Price of a product cannot be less than 1 rupee")
    private double price;

    @Min(value = 0, message = "Stock quantity cannot be less than 0")
    private int stock;
}
