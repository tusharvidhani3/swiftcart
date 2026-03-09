package com.swiftcart.swiftcart.features.order;

import java.util.List;

import org.mapstruct.Mapper;

import com.swiftcart.swiftcart.features.payment.PaymentDto;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {

    OrderResponse toResponse(Order order, List<OrderItem> items, PaymentDto payment);

    OrderResponseForSeller toResponseForSeller(Order order, List<OrderItem> items, PaymentDto payment);

    OrderStats toStats(OrderStatsProjection orderStatsProjection);

}
