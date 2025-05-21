package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;
    private ProductDTO product;
    private int quantity;
}
