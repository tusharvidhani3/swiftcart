package com.swiftcart.swiftcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductDTO;
import com.swiftcart.swiftcart.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public Page<ProductDTO> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy) {
        Pageable pageable=PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProductDTO> productPage=productService.getAllProducts(pageable);
        return productPage;
    }
    
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId) {
        ProductDTO productDTO=productService.getProductById(productId);
        return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
    }

    @PostMapping("/seller/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestPart CreateProductRequest createProductRequest, @RequestPart MultipartFile image) {
        ProductDTO productDTO=productService.createProduct(createProductRequest, image);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping(path = "/products", params = "keyword")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "productId") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProductDTO> productPage = productService.searchProducts(keyword, pageable);
        return new ResponseEntity<Page<ProductDTO>>(productPage, HttpStatus.OK);
    }
}
