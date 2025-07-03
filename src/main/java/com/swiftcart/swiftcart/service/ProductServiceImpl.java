package com.swiftcart.swiftcart.service;

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

import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductResponse;
import com.swiftcart.swiftcart.repository.ProductRepo;

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

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest createProductRequest, List<MultipartFile> productImages) {
        Product product=new Product();
        product.setProductName(createProductRequest.getProductName());
        product.setMrp(createProductRequest.getMrp());
        product.setCategory(createProductRequest.getCategory());
        List<String> relativePaths = productImages.stream().map(productImage -> {
            String fullPath = storageService.store(productImage, uploadDir);
            int relativeStartIndex = fullPath.indexOf("uploads/");
            return fullPath.substring(relativeStartIndex);
        }).collect(Collectors.toList());
        product.setImageUrls(relativePaths);
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

}
