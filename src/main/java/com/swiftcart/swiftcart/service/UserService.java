package com.swiftcart.swiftcart.service;

import com.swiftcart.swiftcart.payload.LoginRequest;
import com.swiftcart.swiftcart.payload.RegisterRequest;
import com.swiftcart.swiftcart.payload.UserDTO;
import com.swiftcart.swiftcart.security.UserDetailsImpl;

public interface UserService {

    public void register(RegisterRequest registerRequest);
    public UserDetailsImpl authenticate(LoginRequest loginRequest);
    public void updateUser(UserDTO userDTO);
}
