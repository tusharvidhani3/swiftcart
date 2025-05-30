package com.swiftcart.swiftcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.payload.AddToCartRequest;
import com.swiftcart.swiftcart.payload.BuyNowPreview;
import com.swiftcart.swiftcart.payload.CartResponse;
import com.swiftcart.swiftcart.payload.UpdateCartItemQtyRequest;
import com.swiftcart.swiftcart.security.UserDetailsImpl;
import com.swiftcart.swiftcart.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return cartService.getCartResponse(userDetailsImpl.getUser().getUserId());
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addProductToCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestBody @Valid AddToCartRequest addToCartRequest) {
        CartResponse cartResponse=cartService.addProductToCart(userDetailsImpl.getUser(), addToCartRequest.getProductId(), addToCartRequest.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQty(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId, @RequestBody @Valid UpdateCartItemQtyRequest req){
        return ResponseEntity.ok(cartService.updateQuantity(userDetailsImpl.getUser().getUserId(), cartItemId, req.getQuantity()));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId) {
        CartResponse cartResponse = cartService.removeProductFromCart(userDetailsImpl.getUser().getUserId(), cartItemId);
        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping("/checkout/buy-now/product/{productId}")
    public ResponseEntity<BuyNowPreview> buyNow(@PathVariable Long productId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        BuyNowPreview buyNowPreview = cartService.createBuyNowPreview(productId, userDetailsImpl.getUser());
        return new ResponseEntity<>(buyNowPreview, HttpStatus.OK);
    }

}
