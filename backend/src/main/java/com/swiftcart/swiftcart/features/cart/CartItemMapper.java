package com.swiftcart.swiftcart.features.cart;

import org.mapstruct.Mapper;

import com.swiftcart.swiftcart.features.product.ProductMapper;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface CartItemMapper {

    CartItemResponse toResponse(CartItem item);
}
