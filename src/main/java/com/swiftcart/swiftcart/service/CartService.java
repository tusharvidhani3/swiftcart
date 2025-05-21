package com.swiftcart.swiftcart.service;

import java.util.List;

import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartItemDTO;

public interface CartService {

    public CartItemDTO addProductToCart(User user, Long productId);
    public boolean removeProductFromCart(Long userId, Long cartItemId);
    public int incrementQuantity(Long userId, Long cartItemId);
    public int decrementQuantity(Long userId, Long cartItemId);
    public List<CartItemDTO> getCartItems(Long userId);
    public Cart createNewCartForUser(User user);
    public BuyNowPreview createBuyNowPreview(Long productId, User user);
}
