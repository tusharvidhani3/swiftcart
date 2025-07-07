package com.swiftcart.swiftcart.features.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    public ProductResponse createProduct(CreateProductRequest createProductRequest, List<MultipartFile> productImages);
    public void deleteProduct(Long productId);
    public Page<ProductResponse> getAllProducts(Pageable pageable);
    public ProductResponse getProduct(Long productId);
    public ProductResponse updateProduct(Long productId, CreateProductRequest productRequest);
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable);
    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable);
    public ProductResponse updateStock(Long productId, int change);
    public Product getProductById(Long productId);
}
