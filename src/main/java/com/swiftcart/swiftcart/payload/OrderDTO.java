package com.swiftcart.swiftcart.payload;

import java.time.LocalDate;

import com.swiftcart.swiftcart.entity.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private AddressSnapshot shippingAddress;
    private Double totalAmount;
    private OrderStatus orderStatus;
    private LocalDate orderDate;
}
