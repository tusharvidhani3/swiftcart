package com.swiftcart.swiftcart.features.cart;

import java.util.List;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface CartService {

    public CartResponse addProductToCart(AppUser user, Long productId, int quantity);
    public CartResponse removeProductFromCart(Long userId, Long cartItemId);
    public CartResponse updateQuantity(Long userId, Long cartItemId, int quantity);
    public CartResponse getCartResponse(Long userId);
    public CartResponse initiateBuyNow(Long productId, AppUser user);
    public int getCartQuantityCount(Long userId);
    List<CartItem> getCartItemsByUserId(Long userId);
    void deleteCartItemsByUserId(Long userId);
    CartItem getCartItemByCartItemId(Long cartItemId);
}
