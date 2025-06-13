package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private Long orderId;
    private LocalDateTime placedAt;
    private ProductSnapshot product;
    private int quantity;
}
