package com.swiftcart.swiftcart.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Customer;
import com.swiftcart.swiftcart.entity.Role;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.CustomerDTO;
import com.swiftcart.swiftcart.payload.CustomerRegisterRequest;
import com.swiftcart.swiftcart.repository.CustomerRepo;
import com.swiftcart.swiftcart.repository.RoleRepo;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public CustomerDTO getCustomer(Long userId) {
        Customer customer = getCustomerByUserId(userId);
        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
        modelMapper.map(customer.getUser(), CustomerDTO.class);
        return customerDTO;
    }

    @Override
    @Transactional
    public void register(CustomerRegisterRequest registerRequest) {
        User user = modelMapper.map(registerRequest, User.class);
        Role role=roleRepo.getRoleByName("ROLE_CUSTOMER");
        user.getRoles().add(role);
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        Customer customer = new Customer();
        customer.setUser(user);
        customerRepo.save(customer);
    }

    @Override
    public Customer getCustomerByUserId(Long userId) {
        Customer customer = customerRepo.findByUser_UserId(userId).orElseThrow(() -> new ResourceNotFoundException("No customer found with this userId"));
        return customer;
    }
}
