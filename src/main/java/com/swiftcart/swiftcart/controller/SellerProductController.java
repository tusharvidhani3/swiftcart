package com.swiftcart.swiftcart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.payload.SellerProductResponse;
import com.swiftcart.swiftcart.payload.UpdateProductStockRequest;
import com.swiftcart.swiftcart.security.UserDetailsImpl;
import com.swiftcart.swiftcart.service.SellerProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/seller-products")
public class SellerProductController {

    @Autowired
    SellerProductService sellerProductService;

    @PatchMapping("/{sellerProductId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProductResponse> updateProductStock(@RequestBody @Valid UpdateProductStockRequest req, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if(sellerProductService.getUserIdBySellerProductId(req.getSellerProductId()) != userDetailsImpl.getUser().getUserId())
        throw new AccessDeniedException("Unauthorized access to this seller's product");
        SellerProductResponse sellerProductResponse = sellerProductService.updateStock(req.getSellerProductId(), req.getChangeInQty());
        return ResponseEntity.ok(sellerProductResponse);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<SellerProductResponse>> getAllSellerProductsOfProduct(@PathVariable Long productId) {
        List<SellerProductResponse> sellerProducts = sellerProductService.getSellerProductsByProductId(productId);
        return ResponseEntity.ok(sellerProducts);
    }
}
