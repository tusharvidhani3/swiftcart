package com.swiftcart.swiftcart.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.entity.Address;
import com.swiftcart.swiftcart.entity.Cart;
import com.swiftcart.swiftcart.entity.CartItem;
import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.payload.AddressDTO;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartItemDTO;
import com.swiftcart.swiftcart.repository.AddressRepo;
import com.swiftcart.swiftcart.repository.CartItemRepo;
import com.swiftcart.swiftcart.repository.CartRepo;
import com.swiftcart.swiftcart.repository.ProductRepo;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepo addressRepo;

    @Override
    public CartItemDTO addProductToCart(User user, Long productId) {
        Cart cart=cartRepo.findByUser(user);
        if(cart==null)
        cart=createNewCartForUser(user);
        Optional<CartItem> cartItemOptional=cartItemRepo.findByCart_User_UserIdAndProduct_ProductId(user.getUserId(), productId);
        if(cartItemOptional.isPresent())
        return modelMapper.map(cartItemOptional.get(), CartItemDTO.class);
        Product product=productRepo.findByProductId(productId);
        CartItem cartItem = new CartItem(product, 1, cart);
        cartItemRepo.save(cartItem);
        return modelMapper.map(cartItem, CartItemDTO.class);
    }

    @Override
    @PreAuthorize("#userId == principal.user.userId")
    public boolean removeProductFromCart(Long userId, Long cartItemId) {
        int rowsAffected=cartItemRepo.deleteByCartItemId(cartItemId);
        return rowsAffected>0;
    }

    @Override
    @PreAuthorize("#userId == principal.user.userId")
    public int incrementQuantity(Long userid, Long cartItemId) {
        CartItem cartItem=cartItemRepo.findByCartItemId(cartItemId).get();
        int updatedQty=cartItem.getQuantity()+1;
        cartItem.setQuantity(updatedQty);
        cartItemRepo.save(cartItem);
        return updatedQty;
    }

    @Override
    @PreAuthorize("#userId == principal.user.userId")
    public int decrementQuantity(Long userId, Long cartItemId) {
        CartItem cartItem=cartItemRepo.findByCartItemId(cartItemId).get();
        int updatedQty=cartItem.getQuantity()-1;
        if(updatedQty==0)
        cartItemRepo.deleteByCartItemId(cartItemId);
        else {
            cartItem.setQuantity(updatedQty);
            cartItemRepo.save(cartItem);
        }
        return updatedQty;
    }

    @Override
    public List<CartItemDTO> getCartItems(Long userId) {
        return cartItemRepo.findAllByCart_User_UserId(userId)
        .stream()
        .map(cartItem -> modelMapper.map(cartItem, CartItemDTO.class))
        .collect(Collectors.toList());
    }

    @Override
    public Cart createNewCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepo.save(cart);
    }

    @Override
    public BuyNowPreview createBuyNowPreview(Long productId, User user) {
        Optional<CartItem> cartItemOptional = cartItemRepo.findByCart_User_UserIdAndProduct_ProductId(user.getUserId(), productId);
        CartItem cartItem = cartItemOptional.isPresent() ? cartItemOptional.get() : new CartItem(productRepo.findByProductId(productId), 0, cartRepo.findByUser_UserId(user.getUserId()));
        incrementQuantity(user.getUserId(), cartItem.getCartItemId());
        Address address = addressRepo.findDefaultShippingAddress(user.getUserId());
        BuyNowPreview buyNowPreview = new BuyNowPreview();
        buyNowPreview.setCartItemDTO(modelMapper.map(cartItem, CartItemDTO.class));
        buyNowPreview.setDefaultAddress(modelMapper.map(address, AddressDTO.class));
        return buyNowPreview;
    }
}
