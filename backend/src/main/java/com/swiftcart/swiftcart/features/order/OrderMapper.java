package com.swiftcart.swiftcart.features.order;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);
    OrderResponseForSeller toResponseForSeller(Order order);

}
