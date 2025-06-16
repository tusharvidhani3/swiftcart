package com.swiftcart.swiftcart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.InsufficientStockException;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartResponse;
import com.swiftcart.swiftcart.payload.CartItemDTO;
import com.swiftcart.swiftcart.repository.CartItemRepo;
import com.swiftcart.swiftcart.repository.CartRepo;
import com.swiftcart.swiftcart.repository.ProductRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CartServiceImpl implements CartService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressService addressService;

    @Override
    @Transactional
    public CartResponse addProductToCart(User user, Long productId, int quantity) {
        Optional<Cart> cartOptional = cartRepo.findByUser_UserId(user.getUserId());
        Cart cart = null;
        if (cartOptional.isEmpty()) {
            cart = createNewCartForUser(user);
            return createCartItemAndGenerateCartResponse(cart, productId, quantity);
        }
        else 
        cart = cartOptional.get();
        Optional<CartItem> cartItemOptional = cartItemRepo.findByCart_User_UserIdAndProduct_ProductId(user.getUserId(),
                productId);
        if (cartItemOptional.isEmpty())
            return createCartItemAndGenerateCartResponse(cart, productId, quantity);
        else {
            CartItem cartItem = cartItemOptional.get();
            int updatedQty = cartItem.getQuantity() + quantity;
            if (cartItem.getProduct().getStock() < updatedQty)
            throw new InsufficientStockException("Cannot add more items. Stock limit reached");
            cartItem.setQuantity(updatedQty);
            cartItemRepo.save(cartItem);
            return getCartResponse(user.getUserId());
        }
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepo.findByCartItemId(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getUser().getUserId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        cartItemRepo.deleteByCartItemId(cartItemId);
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepo.findByCartItemId(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getUser().getUserId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        if (cartItem.getProduct().getStock() < quantity)
            throw new InsufficientStockException("Cannot add more items. Stock limit reached");
        cartItemRepo.updateQuantity(cartItemId, quantity);
        entityManager.clear();
    }

    @Override
    public CartResponse getCartResponse(Long userId) {
        double totalPrice = 0;
        List<CartItem> cartItems = cartItemRepo.findAllByCart_User_UserId(userId);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (CartItem ci : cartItems) {
            totalPrice += ci.getProduct().getPrice() * ci.getQuantity();
            cartItemDTOs.add(modelMapper.map(ci, CartItemDTO.class));
        }
        CartResponse cartResponse = new CartResponse();
        Cart cart = cartRepo.findByUser_UserId(userId)
        .orElseGet(() -> new Cart());
        cartResponse.setCartId(cart.getCartId());
        cartResponse.setTotalPrice(totalPrice);
        cartResponse.setCartItems(cartItemDTOs);
        return cartResponse;
    }

    @Transactional
    private Cart createNewCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart = cartRepo.save(cart);
        return cart;
    }

    @Override
    public BuyNowPreview createBuyNowPreview(Long productId, User user) {
        CartItem cartItem = cartItemRepo.findByCart_User_UserIdAndProduct_ProductId(user.getUserId(),
                productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
            ci.setCart(cartRepo.findByUser_UserId(user.getUserId()).orElseGet(() -> createNewCartForUser(user)));
            ci.setProduct(productRepo.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
            ci.setQuantity(1);
            cartItemRepo.save(ci);
            return ci;
                });
        BuyNowPreview buyNowPreview = new BuyNowPreview();
        buyNowPreview.setCartItemDTO(modelMapper.map(cartItem, CartItemDTO.class));
        buyNowPreview.setDefaultAddress(addressService.getDefaultAddressForUser(user.getUserId()));
        return buyNowPreview;
    }

    private CartResponse createCartItemAndGenerateCartResponse(Cart cart, Long productId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        Product product = productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() < quantity)
            throw new InsufficientStockException("You cannot order more quantity than available");

        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItemRepo.save(cartItem);
        CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);
        cartResponse.setCartItems(List.of(modelMapper.map(cartItem, CartItemDTO.class)));
        return cartResponse;
    }

}
