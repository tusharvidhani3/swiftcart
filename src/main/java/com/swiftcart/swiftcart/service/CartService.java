package com.swiftcart.swiftcart.service;

import java.util.List;

import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartResponse;

public interface CartService {

    public CartResponse addProductToCart(Long userId, Long productId, int quantity);
    public void removeProductFromCart(Long userId, Long cartItemId);
    public void updateQuantity(Long userId, Long cartItemId, int quantity);
    public CartResponse getCartResponse(Long userId);
    public BuyNowPreview createBuyNowPreview(Long productId, Long userId);
    public int getCartQuantityCount(Long userId);
    List<CartItem> getCartItemsByCustomerId(Long customerId);
    void deleteCartItemsByCustomerId(Long customerId);
    CartItem getCartItemByCartItemId(Long cartItemId);
}
