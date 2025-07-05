package com.swiftcart.swiftcart.payload;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCartItemQtyRequest {

    @Min(1)
    private int quantity;
}
