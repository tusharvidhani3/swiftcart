package com.swiftcart.swiftcart.features.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.features.address.AddressService;
import com.swiftcart.swiftcart.common.exception.InsufficientStockException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.product.Product;
import com.swiftcart.swiftcart.features.product.ProductService;
import com.swiftcart.swiftcart.features.user.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CartServiceImpl implements CartService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

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
                    ci.setProduct(productService.getProductById(productId));
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
        Product product = productService.getProductById(productId);
        if (product.getStock() < quantity)
            throw new InsufficientStockException("You cannot order more quantity than available");

        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItemRepo.save(cartItem);
        CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);
        cartResponse.setCartItems(List.of(modelMapper.map(cartItem, CartItemDTO.class)));
        return cartResponse;
    }

    public int getCartQuantityCount(Long userId) {
        Long cartId;
        Integer cartQuantityCount;
        try {
        cartId = cartRepo.findByUser_UserId(userId).get().getCartId();
        cartQuantityCount = cartItemRepo.getTotalQuantityByCartId(cartId);
        }
        catch(NoSuchElementException ex) {
            cartQuantityCount = 0;
        }
        return cartQuantityCount!=null?cartQuantityCount:0;
    }

    @Override
    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartItemRepo.findAllByCart_User_UserId(userId);
    }

    @Override
    public void deleteCartItemsByUserId(Long userId) {
        cartItemRepo.deleteAllByCart_User_UserId(userId);
    }

    @Override
    public CartItem getCartItemByCartItemId(Long cartItemId) {
        return cartItemRepo.findByCartItemId(cartItemId).orElseThrow(() -> new ResourceNotFoundException("No cart item found with this cart item id"));
    }

}
