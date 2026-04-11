package com.swiftcart.swiftcart.features.product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.common.exception.InsufficientStockException;
import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.common.storage.StorageService;

@Service
public class ProductService {

    @Autowired
    private StorageService storageService;

    @Value("${app.product.images-dir}")
    private String uploadDir;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ProductImageRepository productImageRepo;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public SellerProductResponse createProduct(ProductRequest productRequest, List<MultipartFile> productImages) {
        Product product=new Product();
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setMrp(productRequest.mrp());
        product.setStock(productRequest.stock());
        product.setCategory(productRequest.category());
        product.setDescription(productRequest.description());
        List<ProductImage> images = new ArrayList<>();
        productImages.stream().forEach(productImage -> {
            String fullPath = storageService.store(productImage, uploadDir);
            int relativeStartIndex = fullPath.indexOf("uploads/");
            String relativePath = fullPath.substring(relativeStartIndex);
            ProductImage image = new ProductImage();
            image.setImageUrl(relativePath);
            images.add(image);
        });
        productImageRepo.saveAllAndFlush(images);
        product = productRepo.save(product);
        SellerProductResponse productResponse = productMapper.toSellerResponse(product);
        return productResponse;
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepo.deleteById(productId);
        productImageRepo.deleteByProductId(productId);
    }

    public ProductResponse getProduct(Long productId) {
        Product product=productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductResponse productResponse = productMapper.toResponse(product);
        return productResponse;
    }

    @Transactional
    public SellerProductResponse updateProduct(Long productId, ProductRequest productRequest, List<MultipartFile> productImages) {
        Product existingProduct = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productMapper.update(productRequest, existingProduct);
        Product updatedProduct = productRepo.save(existingProduct);
        if (productImages != null) {
            productImageRepo.deleteByProductId(productId);
            List<ProductImage> images = new ArrayList<>();
            productImages.stream().forEach(productImage -> {
                String fullPath = storageService.store(productImage, uploadDir);
                int relativeStartIndex = fullPath.indexOf("uploads/");
                String relativePath = fullPath.substring(relativeStartIndex);
                ProductImage image = new ProductImage();
                image.setImageUrl(relativePath);
                images.add(image);
            });
            productImageRepo.saveAllAndFlush(images);
        }
        SellerProductResponse productResponse = productMapper.toSellerResponse(updatedProduct);
        return productResponse;
    }

    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable, List<String> categories, long minPrice, long maxPrice, boolean includeOutOfStock) {
        Page<ProductResponse> productPage = productRepo.searchProducts(keyword, pageable, minPrice, maxPrice, categories, includeOutOfStock).map(product -> productMapper.toResponse(product));
        return productPage;
    }

    public SellerProductResponse getProductForSeller(Long productId) {
        Product product=productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        SellerProductResponse productResponse = productMapper.toSellerResponse(product);
        return productResponse;
    }

    public Page<SellerProductResponse> searchProductsForSeller(String keyword, Pageable pageable, List<String> categories, long minPrice, long maxPrice) {
        Page<SellerProductResponse> productPage = productRepo.searchProducts(keyword, pageable, minPrice, maxPrice, categories, true).map(product -> productMapper.toSellerResponse(product));
        return productPage;
    }

    @Transactional
    public SellerProductResponse updateStock(Long productId, int stock) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() < 0)
            throw new InsufficientStockException("Stock cannot be negative");
        product.setStock(stock);
        product = productRepo.save(product);
        SellerProductResponse productResponse = productMapper.toSellerResponse(product);
        return productResponse;
    }

    public Product getProductById(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return product;
    }

    public List<String> getProductImages(Long productId) {
        return productImageRepo.findByProductId(productId).stream()
                .map(productImage -> productImage.getImageUrl()).collect(Collectors.toList());
    }

    public ProductStats getProductStats() {
        ProductStats productStats = new ProductStats(0);
        long productsOutOfStock = productRepo.countOutOfStockProducts();
        // productStats.setProductsOutOfStock(productsOutOfStock);
        return productStats;
    }

}
