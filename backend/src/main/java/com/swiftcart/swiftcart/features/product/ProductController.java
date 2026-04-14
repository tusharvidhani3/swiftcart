package com.swiftcart.swiftcart.features.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @GetMapping("{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        ProductResponse productResponse = productService.getProduct(productId);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProductResponse> createProduct(@RequestPart @Valid ProductRequest productRequest, @RequestPart List<MultipartFile> productImages) {
        SellerProductResponse productResponse = productService.createProduct(productRequest, productImages);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ProductResponse>>> searchProducts(@RequestParam(defaultValue = "") String keyword, @RequestParam(required = false) List<String> categories, @RequestParam(defaultValue = "0") long minPrice, @RequestParam(defaultValue = "1000000000") long maxPrice, @RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(defaultValue = "false") boolean includeOutOfStock, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size, @RequestParam(defaultValue = "id") String sortBy, PagedResourcesAssembler<ProductResponse> assembler) {
        if(sortBy.equals("relevance"))
        sortBy = "id";
        Pageable pageable = PageRequest.of(page, size, sortOrder.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy));
        Page<ProductResponse> products = productService.searchProducts(keyword, pageable, categories, minPrice, maxPrice, includeOutOfStock);
        return ResponseEntity.ok(assembler.toModel(products));
    }

    @GetMapping("{productId}/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PagedModel<EntityModel<SellerProductResponse>>> searchProductsForSeller(@RequestParam(defaultValue = "") String keyword, @RequestParam(required = false) List<String> categories, @RequestParam(defaultValue = "0") long minPrice, @RequestParam(defaultValue = "1000000000") long maxPrice, @RequestParam(defaultValue = "asc") String sortOrder, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size, @RequestParam(defaultValue = "id") String sortBy, PagedResourcesAssembler<SellerProductResponse> assembler) {
        Pageable pageable = PageRequest.of(page, size, sortOrder.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy));
        Page<SellerProductResponse> products = productService.searchProductsForSeller(keyword, pageable, categories, minPrice, maxPrice);
        return ResponseEntity.ok(assembler.toModel(products));
    }

    @GetMapping("seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProductResponse> getProductForSeller(@PathVariable Long productId) {
        SellerProductResponse productResponse = productService.getProductForSeller(productId);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProductResponse> updateProduct(@PathVariable Long productId, @RequestPart(required = false) @Valid ProductRequest productRequest, @RequestPart(required = false) List<MultipartFile> productImages) {
        SellerProductResponse productResponse = productService.updateProduct(productId, productRequest, productImages);
        return ResponseEntity.ok(productResponse);
    }

    @PatchMapping("{productId}/stock")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProductResponse> updateProductStock(@PathVariable Long productId, @RequestBody @Valid UpdateProductStockRequest req) {
        SellerProductResponse productResponse = productService.updateStock(productId, req.stock());
        return ResponseEntity.ok(productResponse);
    }
}
