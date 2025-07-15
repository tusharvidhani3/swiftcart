package com.swiftcart.swiftcart.features.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;
import com.swiftcart.swiftcart.features.auth.LoginRequest;

public interface UserService {

    public void register(LoginRequest registerRequest);
    public UserDetailsImpl authenticate(LoginRequest loginRequest);
    public UserDTO updateUser(UserDTO userDTO);
    public Page<UserDTO> getAllUsers(Pageable pageable);
}
