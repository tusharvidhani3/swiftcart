package com.swiftcart.swiftcart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.SellerProduct;
import com.swiftcart.swiftcart.exception.InsufficientStockException;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.SellerProductResponse;
import com.swiftcart.swiftcart.repository.SellerProductRepo;

@Service
public class SellerProductServiceImpl implements SellerProductService {

    @Autowired
    SellerProductRepo sellerProductRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public SellerProductResponse updateStock(Long sellerProductId, int change) {
        SellerProduct sellerProduct = sellerProductRepo.findBySellerProductId(sellerProductId).orElseThrow(() -> new ResourceNotFoundException("SellerProduct not found"));
        if(sellerProduct.getStock() + change < 0)
        throw new InsufficientStockException("Stock cannot be negative");
        sellerProduct.setStock(sellerProduct.getStock() + change);
        sellerProduct = sellerProductRepo.save(sellerProduct);
        return modelMapper.map(sellerProduct, SellerProductResponse.class);
    }

    @Override
    public Long getUserIdBySellerProductId(Long sellerProductId) {
        return sellerProductRepo.findUser_UserIdBySellerProductId(sellerProductId);
    }

    @Override
    public List<SellerProductResponse> getSellerProductsByProductId(Long productId) {
        List<SellerProductResponse> sellerProducts = sellerProductRepo.findAllByProduct_ProductId(productId).stream().map(sellerProduct -> modelMapper.map(sellerProduct, SellerProductResponse.class)).collect(Collectors.toList());
        return sellerProducts;
    }
}
