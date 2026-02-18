package com.swiftcart.swiftcart.features.order;

import java.util.List;

import org.mapstruct.Mapper;

import com.swiftcart.swiftcart.features.product.ProductMapper;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface OrderItemMapper {
    
    OrderItemResponse toResponse(OrderItem orderItem);
    List<OrderItemResponse> toResponse(List<OrderItem> orderItems);
}
