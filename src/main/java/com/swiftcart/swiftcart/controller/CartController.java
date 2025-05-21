package com.swiftcart.swiftcart.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartItemDTO;
import com.swiftcart.swiftcart.security.entity.UserDetailsImpl;
import com.swiftcart.swiftcart.service.CartService;

@RestController
@RequestMapping("/api/customer/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping
    public List<CartItemDTO> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return cartService.getCartItems(userDetailsImpl.getUser().getUserId());
    }
    
    @PostMapping("/products/{productId}")
    public ResponseEntity<CartItemDTO> addProductToCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long productId) {
        CartItemDTO cartItemDTO=cartService.addProductToCart(userDetailsImpl.getUser(), productId);
        return ResponseEntity.created(URI.create("/customer/cart/items/"+cartItemDTO.getCartItemId())).body(cartItemDTO);
    }

    @PutMapping("/{cartItemId}/inc-qty")
    public ResponseEntity<Integer> incrementQty(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.incrementQuantity(userDetailsImpl.getUser().getUserId(), cartItemId));
    }

    @PutMapping("/{cartItemId}/dec-qty")
    public ResponseEntity<Integer> decrementQty(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.decrementQuantity(userDetailsImpl.getUser().getUserId(), cartItemId));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeProductFromCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId) {
        if(cartService.removeProductFromCart(userDetailsImpl.getUser().getUserId(), cartItemId))
        return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found in cart"));
    }

    @PostMapping("/checkout/buy-now/{productId}")
    public ResponseEntity<BuyNowPreview> buyNow(@PathVariable Long productId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        BuyNowPreview buyNowPreview = cartService.createBuyNowPreview(productId, userDetailsImpl.getUser());
        return new ResponseEntity<>(buyNowPreview, HttpStatus.OK);
    }

}
