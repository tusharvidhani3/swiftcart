package com.swiftcart.swiftcart.features.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.common.exception.InsufficientStockException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.appuser.AppUser;
import com.swiftcart.swiftcart.features.product.Product;
import com.swiftcart.swiftcart.features.product.ProductMapper;
import com.swiftcart.swiftcart.features.product.ProductResponse;
import com.swiftcart.swiftcart.features.product.ProductService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartResponse addProductToCart(AppUser user, Long productId, int quantity) {
        Optional<Cart> cartOptional = cartRepo.findByUserId(user.getId());
        Cart cart = null;
        CartItem cartItem = null;
        if (cartOptional.isEmpty()) {
            cart = createNewCartForUser(user);
            cartItem = createCartItem(cart, productId, quantity);
        }
        else 
        cart = cartOptional.get();
        Optional<CartItem> cartItemOptional = cartItemRepo.findByCartUserIdAndProductId(user.getId(),
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
        return getCartResponse(user.getId());
    }

    @Override
    @Transactional
    public CartResponse removeProductFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getUser().getId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        cartItemRepo.delete(cartItem);
        return getCartResponse(userId);
    }

    @Override
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

    @Override
    public CartResponse getCartResponse(Long userId) {
        double totalPrice = 0;
        List<CartItem> cartItems = cartItemRepo.findByCartUserId(userId);
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        for (CartItem ci : cartItems) {
            totalPrice += ci.getProduct().getPrice() * ci.getQuantity();
            List<String> productImages = productService.getProductImages(ci.getProduct().getId());
            ProductResponse productResponse = productMapper.toResponse(ci.getProduct());
            productResponse.setImageUrls(productImages);
            CartItemResponse cartItemResponse = cartItemMapper.toResponse(ci);
            cartItemResponse.setProduct(productResponse);
            cartItemResponses.add(cartItemResponse);
        }
        CartResponse cartResponse = new CartResponse();
        Cart cart = cartRepo.findByUserId(userId)
        .orElseGet(() -> new Cart());
        cartResponse.setId(cart.getId());
        cartResponse.setTotalPrice(totalPrice);
        cartResponse.setCartItems(cartItemResponses);
        return cartResponse;
    }

    @Transactional
    private Cart createNewCartForUser(AppUser user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart = cartRepo.save(cart);
        return cart;
    }

    @Override
    public CartResponse initiateBuyNow(Long productId, AppUser user) {
        CartItem cartItem = cartItemRepo.findByCartUserIdAndProductId(user.getId(),
                productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cartRepo.findByUserId(user.getId()).orElseGet(() -> createNewCartForUser(user)));
                    Product product = productService.getProductById(productId);
                    if(product.getStock() < 1)
                    throw new InsufficientStockException("Product is out of stock");
                    ci.setProduct(product);
                    ci.setQuantity(1);
                    cartItemRepo.save(ci);
                    return ci;
                });

        CartItemResponse cartItemResponse = cartItemMapper.toResponse(cartItem);
        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItems(List.of(cartItemResponse));
        cartResponse.setTotalPrice(cartItemResponse.getProduct().getPrice());
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

    @Override
    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartItemRepo.findByCartUserId(userId);
    }

    @Override
    public void deleteCartItemsByUserId(Long userId) {
        cartItemRepo.deleteByCartUserId(userId);
    }

    @Override
    public CartItem getCartItemByCartItemId(Long cartItemId) {
        return cartItemRepo.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("No cart item found with this cart item id"));
    }

}
