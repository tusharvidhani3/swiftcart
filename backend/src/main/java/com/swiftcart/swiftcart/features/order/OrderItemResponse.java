package com.swiftcart.swiftcart.features.order;

import java.time.LocalDateTime;

import com.swiftcart.swiftcart.features.product.ProductResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private LocalDateTime deliveryAt;
    private ProductResponse product;
    private OrderStatus orderItemStatus;
    private int quantity;
}
