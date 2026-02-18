package com.swiftcart.swiftcart.features.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.common.security.UserPrincipal;
import com.swiftcart.swiftcart.features.auth.AuthRequest;

public interface AppUserService {

    public void register(AuthRequest registerRequest);
    public UserPrincipal authenticate(AuthRequest loginRequest);
    public AppUserDto updateUser(Long userId, AppUserDto userDto);
    public Page<AppUserDto> getAllUsers(Pageable pageable);
}
