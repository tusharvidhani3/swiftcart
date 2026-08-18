package com.swiftcart.swiftcart.features.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.common.exception.InsufficientStockException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.appuser.AppUser;
import com.swiftcart.swiftcart.features.appuser.AppUserRepository;
import com.swiftcart.swiftcart.features.order.ShippingService;
import com.swiftcart.swiftcart.features.product.Product;
import com.swiftcart.swiftcart.features.product.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepo;

    private final CartRepository cartRepo;

    private final ProductService productService;

    private final ShippingService shippingService;

    private final CartItemMapper cartItemMapper;

    private final AppUserRepository userRepo;

    @Transactional
    public CartResponse addProductToCart(Long userId, Long productId, int quantity) {
        Optional<Cart> cartOptional = cartRepo.findByUserId(userId);
        Cart cart = null;
        CartItem cartItem = null;
        if (cartOptional.isEmpty()) {
            cart = createNewCartForUser(userId);
            cartItem = createCartItem(cart, productId, quantity);
        }
        else 
        cart = cartOptional.get();
        Optional<CartItem> cartItemOptional = cartItemRepo.findByCartUserIdAndProductId(userId,
                productId);
        if (cartItemOptional.isEmpty())
            cartItem = createCartItem(cart, productId, quantity);
        else {
            cartItem = cartItemOptional.get();
            int updatedQty = cartItem.getQuantity() + quantity;
            if (cartItem.getProduct().getStock() < updatedQty)
            throw new InsufficientStockException("Cannot add more items. Stock limit reached");
            cartItem.setQuantity(updatedQty);
            cartItemRepo.save(cartItem);
        }
        return getCartResponse(userId);
    }

    @Transactional
    public CartResponse removeProductFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getUser().getId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        cartItemRepo.delete(cartItem);
        return getCartResponse(userId);
    }

    @Transactional
    public CartResponse updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getUser().getId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        if (cartItem.getProduct().getStock() < quantity)
            throw new InsufficientStockException("Cannot add more items. Stock limit reached");
        cartItem.setQuantity(quantity);
        cartItemRepo.save(cartItem);
        return getCartResponse(userId);
    }

    public CartResponse getCartResponse(Long userId) {
        List<CartItem> cartItems = cartItemRepo.findByCartUserId(userId);
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        long subtotal = 0;
        for (CartItem ci : cartItems) {
            subtotal += ci.getProduct().getPrice() * ci.getQuantity();
            CartItemResponse cartItemResponse = cartItemMapper.toResponse(ci);
            cartItemResponses.add(cartItemResponse);
        }
        Cart cart = cartRepo.findByUserId(userId).orElseGet(() -> new Cart());
        CartResponse cartResponse = new CartResponse(cart.getId(), cartItemResponses, subtotal);
        return cartResponse;
    }

    @Transactional
    private Cart createNewCartForUser(Long userId) {
        AppUser user = userRepo.getReferenceById(userId);
        Cart cart = new Cart();
        cart.setUser(user);
        cart = cartRepo.save(cart);
        return cart;
    }

    public CartResponse initiateBuyNow(Long productId, Long userId) {
        CartItem cartItem = cartItemRepo.findByCartUserIdAndProductId(userId, productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cartRepo.findByUserId(userId).orElseGet(() -> createNewCartForUser(userId)));
                    Product product = productService.getProductById(productId);
                    if(product.getStock() < 1)
                    throw new InsufficientStockException("Product is out of stock");
                    ci.setProduct(product);
                    ci.setQuantity(1);
                    cartItemRepo.save(ci);
                    return ci;
                });

        CartItemResponse cartItemResponse = cartItemMapper.toResponse(cartItem);
        CartResponse cartResponse = new CartResponse(null, List.of(cartItemResponse), cartItemResponse.product().price());
        return cartResponse;
    }

    private CartItem createCartItem(Cart cart, Long productId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        Product product = productService.getProductById(productId);
        if (product.getStock() < quantity)
            throw new InsufficientStockException("You cannot order more quantity than available");
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItemRepo.save(cartItem);
    }

    public int getCartQuantityCount(Long userId) {
        Long cartId;
        Integer cartQuantityCount;
        try {
        cartId = cartRepo.findByUserId(userId).get().getId();
        cartQuantityCount = cartItemRepo.sumQuantityByCartId(cartId);
        }
        catch(NoSuchElementException ex) {
            cartQuantityCount = 0;
        }
        return cartQuantityCount!=null?cartQuantityCount:0;
    }

    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartItemRepo.findByCartUserId(userId);
    }

    public void deleteCartItemsByUserId(Long userId) {
        cartItemRepo.deleteByCartUserId(userId);
    }

    public CartItem getCartItemByCartItemId(Long cartItemId) {
        return cartItemRepo.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("No cart item found with this cart item id"));
    }

    public CartSummary getCartSummary(Long userId) {
        List<CartItem> cartItems = cartItemRepo.findByCartUserId(userId);
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        long subtotal = 0;
        for (CartItem ci : cartItems) {
            subtotal += ci.getProduct().getPrice() * ci.getQuantity();
            CartItemResponse cartItemResponse = cartItemMapper.toResponse(ci);
            cartItemResponses.add(cartItemResponse);
        }
        long shippingCharge = shippingService.calculate(subtotal);
        Cart cart = cartRepo.findByUserId(userId).get();
        CartSummary cartSummary = new CartSummary(cart.getId(), cartItemResponses, subtotal, shippingCharge, subtotal + shippingCharge);
        return cartSummary;
    }

}
