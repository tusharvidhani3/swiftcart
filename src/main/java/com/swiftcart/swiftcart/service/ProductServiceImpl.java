package com.swiftcart.swiftcart.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.entity.Product;
import com.swiftcart.swiftcart.payload.CreateProductRequest;
import com.swiftcart.swiftcart.payload.ProductDTO;
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
    public ProductDTO createProduct(CreateProductRequest createProductRequest, MultipartFile image) {
        Product product=new Product();
        product.setProductName(createProductRequest.getProductName());
        product.setPrice(createProductRequest.getPrice());
        product.setImage(storageService.store(image, uploadDir));
        product.setCategory(createProductRequest.getCategory());
        productRepo.save(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepo.deleteByProductId(productId);
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<ProductDTO> productDTOPage=productRepo.findAll(pageable).map(product -> modelMapper.map(product, ProductDTO.class));
        return productDTOPage;
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product=productRepo.findByProductId(productId);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product existingProduct = productRepo.findByProductId(productDTO.getProductId());
        modelMapper.map(productDTO, existingProduct);
        Product updatedProduct = productRepo.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        Page<ProductDTO> productPage = productRepo.searchProducts(keyword, pageable).map(product -> modelMapper.map(product, ProductDTO.class));
        return productPage;
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        Page<ProductDTO> productPage = productRepo.findByCategory(category, pageable);
        return productPage;
    }

}
