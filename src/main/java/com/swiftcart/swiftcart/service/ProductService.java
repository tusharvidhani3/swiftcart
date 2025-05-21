package com.swiftcart.swiftcart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductDTO;

public interface ProductService {

    public ProductDTO createProduct(CreateProductRequest createProductRequest, MultipartFile image);
    public void deleteProduct(Long productId);
    public Page<ProductDTO> getAllProducts(Pageable pageable);
    public ProductDTO getProductById(Long productId);
    public ProductDTO updateProduct(ProductDTO productDTO);
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable);
    public Page<ProductDTO> getProductsByCategory(String categoryId, Pageable pageable);
    // void updateProductQuantity(Long productId, int quantityChange);
}
