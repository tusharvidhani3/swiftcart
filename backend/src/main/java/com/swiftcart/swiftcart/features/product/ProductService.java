package com.swiftcart.swiftcart.features.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    public ProductResponse createProduct(CreateProductRequest createProductRequest, List<MultipartFile> productImages);
    public void deleteProduct(Long productId);
    public ProductResponse getProduct(Long productId);
    public ProductResponse updateProduct(Long productId, UpdateProductRequest productRequest, List<MultipartFile> productImages);
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable, List<String> categories, long minPrice, long maxPrice, boolean includeOutOfStock);
    public ProductResponse updateStock(Long productId, int stock);
    public Product getProductById(Long productId);
    public List<String> getProductImages(Long productId);
}
