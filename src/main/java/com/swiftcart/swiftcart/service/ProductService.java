package com.swiftcart.swiftcart.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductResponse;

public interface ProductService {

    public ProductResponse createProduct(CreateProductRequest createProductRequest, List<MultipartFile> productImages);
    public void deleteProduct(Long productId);
    public Page<ProductResponse> getAllProducts(Pageable pageable);
    public ProductResponse getProductById(Long productId);
    public ProductResponse updateProduct(Long productId, CreateProductRequest productRequest);
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable);
    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable);
}
