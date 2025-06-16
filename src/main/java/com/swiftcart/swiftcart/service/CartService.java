package com.swiftcart.swiftcart.service;

import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartResponse;

public interface CartService {

    public CartResponse addProductToCart(User user, Long productId, int quantity);
    public void removeProductFromCart(Long userId, Long cartItemId);
    public void updateQuantity(Long userId, Long cartItemId, int quantity);
    public CartResponse getCartResponse(Long userId);
    public BuyNowPreview createBuyNowPreview(Long productId, User user);
    public int getCartQuantityCount(User user);
}
