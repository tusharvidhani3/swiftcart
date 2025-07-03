package com.swiftcart.swiftcart.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Role;
import com.swiftcart.swiftcart.entity.Seller;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.payload.SellerDTO;
import com.swiftcart.swiftcart.payload.SellerRegisterRequest;
import com.swiftcart.swiftcart.repository.RoleRepo;
import com.swiftcart.swiftcart.repository.SellerRepo;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepo sellerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public SellerDTO getSeller(Long userId) {
        Seller seller = sellerRepo.findByUser_UserId(userId).orElseThrow(() -> new ResourceNotFoundException("No seller found with this userId"));
        SellerDTO sellerDTO = modelMapper.map(seller, SellerDTO.class);
        modelMapper.map(seller.getUser(), SellerDTO.class);
        return sellerDTO;
    }

    @Override
    @Transactional
    public void register(SellerRegisterRequest registerRequest) {
        User user = modelMapper.map(registerRequest, User.class);
        Role role=roleRepo.getRoleByName("ROLE_SELLER");
        user.getRoles().add(role);
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        Seller seller = new Seller();
        seller.setUser(user);
        sellerRepo.save(seller);
    }
}
