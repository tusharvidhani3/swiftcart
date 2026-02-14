package com.swiftcart.swiftcart.features.order;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    OrderItemResponse toResponse(OrderItem orderItem);
}
