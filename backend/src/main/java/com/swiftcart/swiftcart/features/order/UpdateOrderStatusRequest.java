package com.swiftcart.swiftcart.features.order;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    private Long orderId;
    private OrderStatus orderStatus;
}
