package com.swiftcart.swiftcart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.entity.Customer;
import com.swiftcart.swiftcart.entity.SellerProduct;
import com.swiftcart.swiftcart.exception.IllegalActionException;
import com.swiftcart.swiftcart.exception.InsufficientStockException;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartResponse;
import com.swiftcart.swiftcart.payload.CartItemDTO;
import com.swiftcart.swiftcart.repository.CartItemRepo;
import com.swiftcart.swiftcart.repository.CartRepo;
import com.swiftcart.swiftcart.repository.SellerProductRepo;

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
    private CustomerService customerService;

    @Autowired
    private SellerProductRepo sellerProductRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

    @Override
    @Transactional
    public CartResponse addProductToCart(Long userId, Long sellerProductId, int quantity) {
        Customer customer = customerService.getCustomerByUserId(userId);
        Optional<Cart> cartOptional = cartRepo.findByCustomer_CustomerId(customer.getCustomerId());
        Cart cart = null;
        if (cartOptional.isEmpty()) {
            cart = createNewCartForCustomer(customer);
            return createCartItemAndGenerateCartResponse(cart, sellerProductId, quantity);
        }
        else 
        cart = cartOptional.get();
        Optional<CartItem> cartItemOptional = cartItemRepo.findByCart_Customer_CustomerIdAndSellerProduct_SellerProductId(customer.getCustomerId(),
                sellerProductId);
        if (cartItemOptional.isEmpty())
            return createCartItemAndGenerateCartResponse(cart, sellerProductId, quantity);
        else {
            CartItem cartItem = cartItemOptional.get();
            int updatedQty = cartItem.getQuantity() + quantity;
            if (cartItem.getSellerProduct().getStock() < updatedQty)
            throw new InsufficientStockException("Cannot add more items. Stock limit reached");
            cartItem.setQuantity(updatedQty);
            cartItemRepo.save(cartItem);
            return getCartResponse(customer.getCustomerId());
        }
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepo.findByCartItemId(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getCustomer().getUser().getUserId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        cartItemRepo.deleteByCartItemId(cartItemId);
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepo.findByCartItemId(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!cartItem.getCart().getCustomer().getUser().getUserId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this cart item");
        if (cartItem.getSellerProduct().getStock() < quantity)
            throw new InsufficientStockException("Cannot add more items. Stock limit reached");
        cartItemRepo.updateQuantity(cartItemId, quantity);
        entityManager.clear();
    }

    @Override
    public CartResponse getCartResponse(Long customerId) {
        double totalPrice = 0;
        List<CartItem> cartItems = cartItemRepo.findAllByCart_Customer_CustomerId(customerId);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (CartItem ci : cartItems) {
            totalPrice += ci.getSellerProduct().getPrice() * ci.getQuantity();
            cartItemDTOs.add(modelMapper.map(ci, CartItemDTO.class));
        }
        CartResponse cartResponse = new CartResponse();
        Cart cart = cartRepo.findByCustomer_CustomerId(customerId)
        .orElseGet(() -> new Cart());
        cartResponse.setCartId(cart.getCartId());
        cartResponse.setTotalPrice(totalPrice);
        cartResponse.setCartItems(cartItemDTOs);
        return cartResponse;
    }

    @Transactional
    private Cart createNewCartForCustomer(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart = cartRepo.save(cart);
        return cart;
    }

    @Override
    public BuyNowPreview createBuyNowPreview(Long sellerProductId, Long userId) {
        Customer customer = customerService.getCustomerByUserId(userId);
        CartItem cartItem = cartItemRepo.findByCart_Customer_CustomerIdAndSellerProduct_SellerProductId(customer.getCustomerId(),
                sellerProductId)
                .orElseGet(() -> {
                    SellerProduct sellerProduct = sellerProductRepo.findBySellerProductId(sellerProductId).orElseThrow(() -> new ResourceNotFoundException("Seller's product not found"));
                    if(sellerProduct.getSeller().getUser().getUserId() == userId)
                    throw new IllegalActionException("Sellers cannot purchase their own products");
                    
                    CartItem ci = new CartItem();
                    ci.setCart(cartRepo.findByCustomer_CustomerId(customer.getCustomerId()).orElseGet(() -> createNewCartForCustomer(customer)));
                    ci.setSellerProduct(sellerProduct);
                    ci.setQuantity(1);
                    cartItemRepo.save(ci);
                    return ci;
                });
        BuyNowPreview buyNowPreview = new BuyNowPreview();
        buyNowPreview.setCartItemDTO(modelMapper.map(cartItem, CartItemDTO.class));
        buyNowPreview.setDefaultAddress(addressService.getDefaultAddressForUser(userId));
        return buyNowPreview;
    }

    private CartResponse createCartItemAndGenerateCartResponse(Cart cart, Long sellerProductId, int quantity) {
        SellerProduct sellerProduct = sellerProductRepo.findBySellerProductId(sellerProductId)
        .orElseThrow(() -> new ResourceNotFoundException("Seller's Product not found"));

        if(sellerProduct.getSeller().getUser().getUserId() == cart.getCustomer().getUser().getUserId())
            throw new IllegalActionException("Sellers cannot purchase their own products");

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);

        if (sellerProduct.getStock() < quantity)
            throw new InsufficientStockException("You cannot order more quantity than available");

        cartItem.setSellerProduct(sellerProduct);
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
        cartId = cartRepo.findByCustomer_User_UserId(userId).get().getCartId();
        cartQuantityCount = cartItemRepo.getTotalQuantityByCartId(cartId);
        }
        catch(NoSuchElementException ex) {
            cartQuantityCount = 0;
        }
        return cartQuantityCount!=null?cartQuantityCount:0;
    }

    @Override
    public List<CartItem> getCartItemsByCustomerId(Long customerId) {
        return cartItemRepo.findAllByCart_Customer_CustomerId(customerId);
    }

    @Override
    public void deleteCartItemsByCustomerId(Long customerId) {
        cartItemRepo.deleteAllByCart_Customer_CustomerId(customerId);
    }

    @Override
    public CartItem getCartItemByCartItemId(Long cartItemId) {
        return cartItemRepo.findByCartItemId(cartItemId).orElseThrow(() -> new ResourceNotFoundException("No cart item found with this cart item id"));
    }

}
