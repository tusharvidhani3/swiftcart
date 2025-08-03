package com.swiftcart.swiftcart.features.cart;

import java.util.HashMap;
import java.util.Map;

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

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return ResponseEntity.ok(cartService.getCartResponse(userDetailsImpl.getUser().getUserId()));
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addProductToCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestBody @Valid AddToCartRequest addToCartRequest) {
        CartResponse cartResponse=cartService.addProductToCart(userDetailsImpl.getUser(), addToCartRequest.getProductId(), addToCartRequest.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQty(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId, @RequestBody @Valid UpdateCartItemQtyRequest req){
        CartResponse cartResponse = cartService.updateQuantity(userDetailsImpl.getUser().getUserId(), cartItemId, req.getQuantity());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PathVariable Long cartItemId) {
        CartResponse cartResponse = cartService.removeProductFromCart(userDetailsImpl.getUser().getUserId(), cartItemId);
        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping("/checkout/buy-now/product/{productId}")
    public ResponseEntity<CartResponse> buyNow(@PathVariable Long productId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        CartResponse cartResponse = cartService.initiateBuyNow(productId, userDetailsImpl.getUser());
        return ResponseEntity.ok(cartResponse);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartQtyCount(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return ResponseEntity.ok(cartService.getCartQuantityCount(userDetailsImpl.getUser().getUserId()));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getCartSummary(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        CartResponse cartResponse =  cartService.getCartResponse(userDetailsImpl.getUser().getUserId());
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPrice", cartResponse.getTotalPrice());
        summary.put("cartItemsCount", cartResponse.getCartItems().size());
        return ResponseEntity.ok(summary);
    }
    

}
