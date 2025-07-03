package com.swiftcart.swiftcart.service;

import com.swiftcart.swiftcart.entity.Customer;
import com.swiftcart.swiftcart.payload.CustomerDTO;
import com.swiftcart.swiftcart.payload.CustomerRegisterRequest;

public interface CustomerService {

    public CustomerDTO getCustomer(Long userId);
    Customer getCustomerByUserId(Long userId);

    public void register(CustomerRegisterRequest registerRequest);
}
