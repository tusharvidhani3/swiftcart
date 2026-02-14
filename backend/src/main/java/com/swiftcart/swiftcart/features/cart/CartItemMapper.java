package com.swiftcart.swiftcart.features.cart;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItemResponse toResponse(CartItem cartItem);
}
