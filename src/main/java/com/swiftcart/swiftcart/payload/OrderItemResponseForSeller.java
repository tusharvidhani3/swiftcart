package com.swiftcart.swiftcart.payload;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemResponseForSeller {

    private OrderItemResponse orderItemResponse;
    private PaymentDTO payment;
    private Long orderId;
    private String buyerName;
    private LocalDateTime orderDate;
    private AddressDTO address;

}
