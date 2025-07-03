package com.swiftcart.swiftcart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.payload.LoginRequest;
import com.swiftcart.swiftcart.payload.UserDTO;
import com.swiftcart.swiftcart.security.UserDetailsImpl;

public interface UserService {

    public void register(LoginRequest registerRequest);
    public UserDetailsImpl authenticate(LoginRequest loginRequest);
    public void updateUser(UserDTO userDTO);
    public Page<UserDTO> getAllUsers(Pageable pageable);
}
