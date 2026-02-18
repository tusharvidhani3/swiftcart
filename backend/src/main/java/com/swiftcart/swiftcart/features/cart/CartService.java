package com.swiftcart.swiftcart.features.cart;

import java.util.List;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface CartService {
    CartResponse addProductToCart(AppUser user, Long productId, int quantity);
    CartResponse removeProductFromCart(Long userId, Long cartItemId);
    CartResponse updateQuantity(Long userId, Long cartItemId, int quantity);
    CartResponse getCartResponse(Long userId);
    CartResponse initiateBuyNow(Long productId, AppUser user);
    int getCartQuantityCount(Long userId);
    List<CartItem> getCartItemsByUserId(Long userId);
    void deleteCartItemsByUserId(Long userId);
    CartItem getCartItemByCartItemId(Long cartItemId);
}
