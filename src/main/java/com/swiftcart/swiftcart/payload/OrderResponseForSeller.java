package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseForSeller {

    private Long orderId;
    private LocalDateTime placedAt;
    private double totalAmount;
    private PaymentDTO payment;
    private String buyerName;
    private AddressSnapshot address;
    private List<OrderItemResponse> orderItems;

}
