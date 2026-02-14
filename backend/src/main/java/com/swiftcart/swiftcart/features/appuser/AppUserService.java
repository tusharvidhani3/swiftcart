package com.swiftcart.swiftcart.features.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;
import com.swiftcart.swiftcart.features.auth.LoginRequest;

public interface AppUserService {

    public void register(LoginRequest registerRequest);
    public UserDetailsImpl authenticate(LoginRequest loginRequest);
    public AppUserDto updateUser(AppUserDto userDto);
    public Page<AppUserDto> getAllUsers(Pageable pageable);
}
