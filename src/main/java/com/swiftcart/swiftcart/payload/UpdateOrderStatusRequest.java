package com.swiftcart.swiftcart.payload;

import com.swiftcart.swiftcart.entity.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    private Long orderId;
    private OrderStatus orderStatus;
}
