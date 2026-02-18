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

import com.swiftcart.swiftcart.common.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(cartService.getCartResponse(userPrincipal.getUser().getId()));
    }
    
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addProductToCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid AddToCartRequest addToCartRequest) {
        CartResponse cartResponse=cartService.addProductToCart(userPrincipal.getUser(), addToCartRequest.productId(), addToCartRequest.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQty(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long cartItemId, @RequestBody @Valid UpdateCartItemQtyRequest req){
        CartResponse cartResponse = cartService.updateQuantity(userPrincipal.getUser().getId(), cartItemId, req.quantity());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long cartItemId) {
        CartResponse cartResponse = cartService.removeProductFromCart(userPrincipal.getUser().getId(), cartItemId);
        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping("/checkout/buy-now/product/{productId}")
    public ResponseEntity<CartResponse> buyNow(@PathVariable Long productId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse cartResponse = cartService.initiateBuyNow(productId, userPrincipal.getUser());
        return ResponseEntity.ok(cartResponse);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartQtyCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(cartService.getCartQuantityCount(userPrincipal.getUser().getId()));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getCartSummary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse cartResponse =  cartService.getCartResponse(userPrincipal.getUser().getId());
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPrice", cartResponse.totalPrice());
        summary.put("cartItemsCount", cartResponse.items().size());
        return ResponseEntity.ok(summary);
    }
    

}
