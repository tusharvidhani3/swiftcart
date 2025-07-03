package com.swiftcart.swiftcart.service;

import java.util.List;

import com.swiftcart.swiftcart.payload.SellerProductResponse;

public interface SellerProductService {

    public SellerProductResponse updateStock(Long sellerProductId, int stock);

    public Long getUserIdBySellerProductId(Long sellerProductId);

    public List<SellerProductResponse> getSellerProductsByProductId(Long productId);

}
