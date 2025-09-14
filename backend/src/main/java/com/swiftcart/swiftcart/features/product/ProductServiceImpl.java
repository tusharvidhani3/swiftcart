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
        Product product = new Product();
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
        Product product = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        productResponse.setImageUrls(productImageRepo.findAllByProduct_ProductId(productId).stream()
                .map(productImage -> productImage.getImageUrl()).collect(Collectors.toList()));
        return productResponse;
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest productRequest,
            List<MultipartFile> productImages) {
        Product existingProduct = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if(productRequest.getProductName() != null)
        existingProduct.setProductName(productRequest.getProductName());
        if(productRequest.getPrice() != null)
        existingProduct.setPrice(productRequest.getPrice());
        if(productRequest.getMrp() != null)
        existingProduct.setPrice(productRequest.getPrice());
        if(productRequest.getCategory() != null)
        existingProduct.setCategory(productRequest.getCategory());
        if(productRequest.getDescription() != null)
        existingProduct.setDescription(productRequest.getDescription());
        if(productRequest.getStock() != null)
        existingProduct.setStock(productRequest.getStock());

        Product updatedProduct = productRepo.save(existingProduct);
        ProductResponse productResponse = modelMapper.map(updatedProduct, ProductResponse.class);
        if (productImages != null) {
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
            productResponse.setImageUrls(relativePaths);
        }
        return productResponse;
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable, List<String> categories,
            long minPrice, long maxPrice, boolean includeOutOfStock) {
        Page<ProductResponse> productPage = productRepo
                .searchProducts(keyword, pageable, minPrice, maxPrice, categories, includeOutOfStock).map(product -> {
                    ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
                    productResponse.setImageUrls(productImageRepo.findAllByProduct_ProductId(product.getProductId())
                            .stream().map(productImage -> productImage.getImageUrl()).collect(Collectors.toList()));
                    return productResponse;
                });
        return productPage;
    }

    @Override
    @Transactional
    public ProductResponse updateStock(Long productId, int stock) {
        Product product = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() < 0)
            throw new InsufficientStockException("Stock cannot be negative");
        product.setStock(stock);
        product = productRepo.save(product);
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        productResponse.setImageUrls(productImageRepo.findAllByProduct_ProductId(productId).stream()
                .map(productImage -> productImage.getImageUrl()).collect(Collectors.toList()));
        return productResponse;
    }

    @Override
    public Product getProductById(Long productId) {
        Product product = productRepo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return product;
    }

    @Override
    public List<String> getProductImages(Long productId) {
        return productImageRepo.findAllByProduct_ProductId(productId).stream()
                .map(productImage -> productImage.getImageUrl()).collect(Collectors.toList());
    }

}
