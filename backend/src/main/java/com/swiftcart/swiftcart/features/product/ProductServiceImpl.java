package com.swiftcart.swiftcart.features.product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StorageService storageService;

    @Value("${product.images.directory}")
    private String uploadDir;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductImageRepo productImageRepo;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest createProductRequest, List<MultipartFile> productImages) {
        Product product=new Product();
        product.setProductName(createProductRequest.getProductName());
        product.setPrice(createProductRequest.getPrice());
        product.setMrp(createProductRequest.getMrp());
        product.setStock(createProductRequest.getStock());
        product.setCategory(createProductRequest.getCategory());
        product.setDescription(createProductRequest.getDescription());
        List<ProductImage> images = new ArrayList<>();
        List<String> relativePaths = productImages.stream().map(productImage -> {
            String fullPath = storageService.store(productImage, uploadDir);
            int relativeStartIndex = fullPath.indexOf("uploads/");
            String relativePath = fullPath.substring(relativeStartIndex);
            ProductImage image = new ProductImage();
            image.setImageUrl(relativePath);
            images.add(image);
            return relativePath;
        }).collect(Collectors.toList());
        productImageRepo.saveAllAndFlush(images);
        productRepo.save(product);
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        productResponse.setImageUrls(relativePaths);
        return productResponse;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        productRepo.deleteByProductId(productId);
        productImageRepo.deleteAllByProduct_ProductId(productId);
    }

    @Override
    public ProductResponse getProduct(Long productId) {
        Product product=productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        productResponse.setImageUrls(productImageRepo.findAllByProduct_ProductId(productId).stream().map(productImage -> productImage.getImageUrl()).collect(Collectors.toList()));
        return productResponse;
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, CreateProductRequest productRequest, List<MultipartFile> productImages) {
        Product existingProduct = productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        modelMapper.map(productRequest, existingProduct);
        Product updatedProduct = productRepo.save(existingProduct);
        productImageRepo.deleteAllByProduct_ProductId(productId);
        List<ProductImage> images = new ArrayList<>();
        List<String> relativePaths = productImages.stream().map(productImage -> {
            String fullPath = storageService.store(productImage, uploadDir);
            int relativeStartIndex = fullPath.indexOf("uploads/");
            String relativePath = fullPath.substring(relativeStartIndex);
            ProductImage image = new ProductImage();
            image.setImageUrl(relativePath);
            images.add(image);
            return relativePath;
        }).collect(Collectors.toList());
        productImageRepo.saveAllAndFlush(images);
        ProductResponse productResponse = modelMapper.map(updatedProduct, ProductResponse.class);
        productResponse.setImageUrls(relativePaths);
        return productResponse;
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable, String category, long minPrice, long maxPrice, boolean inStock) {
        Page<ProductResponse> productPage = productRepo.searchProducts(keyword, pageable, minPrice, maxPrice, category, inStock).map(product -> {
            ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
            productResponse.setImageUrls(productImageRepo.findAllByProduct_ProductId(product.getProductId()).stream().map(productImage -> productImage.getImageUrl()).collect(Collectors.toList()));
            return productResponse;
        });
        return productPage;
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long productId, int change) {
        Product product = productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if(product.getStock() + change < 0)
        throw new InsufficientStockException("Stock cannot be negative");
        product.setStock(product.getStock() + change);
        product = productRepo.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public Product getProductById(Long productId) {
        Product product = productRepo.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return product;
    }

    @Override
    public List<String> getProductImages(Long productId) {
        return productImageRepo.findAllByProduct_ProductId(productId).stream().map(productImage -> productImage.getImageUrl()).collect(Collectors.toList());
    }

}
