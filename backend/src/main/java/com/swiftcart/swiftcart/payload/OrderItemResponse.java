package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private LocalDateTime placedAt;
    private LocalDateTime deliveryAt;
    private ProductResponse product;
    private int quantity;
}
