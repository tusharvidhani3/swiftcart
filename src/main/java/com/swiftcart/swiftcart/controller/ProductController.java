package com.swiftcart.swiftcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductResponse;
import com.swiftcart.swiftcart.payload.UpdateProductStockRequest;
import com.swiftcart.swiftcart.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public Page<ProductResponse> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy) {
        Pageable pageable=PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProductResponse> productPage=productService.getAllProducts(pageable);
        return productPage;
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        ProductResponse productResponse=productService.getProductById(productId);
        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> createProduct(@RequestPart @Valid CreateProductRequest createProductRequest, @RequestPart MultipartFile productImage) {
        ProductResponse productResponse=productService.createProduct(createProductRequest, productImage);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping(params = "keyword")
    public ResponseEntity<Page<ProductResponse>> searchProducts(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProductResponse> productPage = productService.searchProducts(keyword, pageable);
        return new ResponseEntity<Page<ProductResponse>>(productPage, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId,@RequestBody @Valid CreateProductRequest productRequest) {
        ProductResponse productResponse = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProductStock(@RequestBody @Valid UpdateProductStockRequest req) {
        ProductResponse productResponse = productService.updateStock(req.getProductId(), req.getChangeInQty());
        return ResponseEntity.ok(productResponse);
    }
}
