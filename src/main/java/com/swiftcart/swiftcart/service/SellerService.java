package com.swiftcart.swiftcart.service;

import com.swiftcart.swiftcart.payload.SellerDTO;
import com.swiftcart.swiftcart.payload.SellerRegisterRequest;

public interface SellerService {

    public SellerDTO getSeller(Long userId);
    public void register(SellerRegisterRequest registerRequest);
}
