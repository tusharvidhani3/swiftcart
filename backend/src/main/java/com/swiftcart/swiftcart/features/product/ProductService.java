package com.swiftcart.swiftcart.features.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    SellerProductResponse createProduct(ProductRequest productRequest, List<MultipartFile> productImages);
    void deleteProduct(Long productId);
    ProductResponse getProduct(Long productId);
    SellerProductResponse getProductForSeller(Long productId);
    SellerProductResponse updateProduct(Long productId, ProductRequest productRequest, List<MultipartFile> productImages);
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable, List<String> categories, long minPrice, long maxPrice, boolean includeOutOfStock);
    Page<SellerProductResponse> searchProductsForSeller(String keyword, Pageable pageable, List<String> categories, long minPrice, long maxPrice);
    SellerProductResponse updateStock(Long productId, int stock);
    Product getProductById(Long productId);
    List<String> getProductImages(Long productId);
    ProductStats getProductStats();
}
