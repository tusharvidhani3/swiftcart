package com.swiftcart.swiftcart.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.exception.InsufficientStockException;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductResponse;
import com.swiftcart.swiftcart.repository.ProductRepo;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    StorageService storageService;

    @Value("${product.images.directory}")
    String uploadDir;

    @Autowired
    ProductRepo productRepo;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest createProductRequest, MultipartFile productImage) {
        Product product=new Product();
        product.setProductName(createProductRequest.getProductName());
        product.setPrice(createProductRequest.getPrice());
        product.setCategory(createProductRequest.getCategory());
        product.setStock(0);
        String fullPath = storageService.store(productImage, uploadDir);
        int relativeStartIndex = fullPath.indexOf("uploads/");
        product.setImage(fullPath.substring(relativeStartIndex));
        productRepo.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        // In multi-seller system only allow seller to delete seller-product only if it belongs to that seller and admin to delete product
        productRepo.deleteByProductId(productId);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<ProductResponse> productResponsePage=productRepo.findAll(pageable).map(product -> modelMapper.map(product, ProductResponse.class));
        return productResponsePage;
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product=productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, CreateProductRequest productRequest) {
        Product existingProduct = productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        modelMapper.map(productRequest, existingProduct);
        Product updatedProduct = productRepo.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductResponse.class);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<ProductResponse> productPage = productRepo.searchProducts(keyword, pageable).map(product -> modelMapper.map(product, ProductResponse.class));
        return productPage;
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        Page<ProductResponse> productPage = productRepo.findByCategory(category, pageable);
        return productPage;
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long productId, int change) {
        // In multi-seller system allow only stock field in seller-product and allow change by the owner of seller-product to update the stock
        Product product = productRepo.findByProductId(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if(product.getStock() + change < 0)
        throw new InsufficientStockException("Stock cannot be negative");
        product.setStock(product.getStock() + change);
        product = productRepo.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

}
