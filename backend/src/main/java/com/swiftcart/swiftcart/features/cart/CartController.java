package com.swiftcart.swiftcart.features.cart;

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

import com.swiftcart.swiftcart.common.security.AppUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/carts")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.ok(cartService.getCartResponse(userPrincipal.getUserId()));
    }
    
    @PostMapping("items")
    public ResponseEntity<CartResponse> addProductToCart(@AuthenticationPrincipal AppUserDetails userPrincipal, @RequestBody @Valid AddToCartRequest addToCartRequest) {
        CartResponse cartResponse=cartService.addProductToCart(userPrincipal.getUserId(), addToCartRequest.productId(), addToCartRequest.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @PutMapping("items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQty(@AuthenticationPrincipal AppUserDetails userPrincipal, @PathVariable Long cartItemId, @RequestBody @Valid UpdateCartItemQtyRequest req){
        CartResponse cartResponse = cartService.updateQuantity(userPrincipal.getUserId(), cartItemId, req.quantity());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("items/{cartItemId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@AuthenticationPrincipal AppUserDetails userPrincipal, @PathVariable Long cartItemId) {
        CartResponse cartResponse = cartService.removeProductFromCart(userPrincipal.getUserId(), cartItemId);
        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping("checkout/buy-now/product/{productId}")
    public ResponseEntity<CartResponse> buyNow(@PathVariable Long productId, @AuthenticationPrincipal AppUserDetails userPrincipal) {
        CartResponse cartResponse = cartService.initiateBuyNow(productId, userPrincipal.getUserId());
        return ResponseEntity.ok(cartResponse);
    }

    @GetMapping("count")
    public ResponseEntity<Integer> getCartQtyCount(@AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.ok(cartService.getCartQuantityCount(userPrincipal.getUserId()));
    }

    @GetMapping("summary")
    public ResponseEntity<CartSummary> getCartSummary(@AuthenticationPrincipal AppUserDetails userPrincipal) {
        CartSummary summary =  cartService.getCartSummary(userPrincipal.getUserId());
        return ResponseEntity.ok(summary);
    }
    

}
