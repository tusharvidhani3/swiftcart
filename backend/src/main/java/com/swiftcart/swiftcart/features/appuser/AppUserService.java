package com.swiftcart.swiftcart.features.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;
import com.swiftcart.swiftcart.features.auth.AuthRequest;

public interface AppUserService {

    public void register(AuthRequest registerRequest);
    public UserDetailsImpl authenticate(AuthRequest loginRequest);
    public AppUserDto updateUser(AppUserDto userDto);
    public Page<AppUserDto> getAllUsers(Pageable pageable);
}
