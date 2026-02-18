package com.swiftcart.swiftcart.features.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.swiftcart.common.security.UserPrincipal;
import com.swiftcart.swiftcart.features.auth.AuthRequest;

public interface AppUserService {

    void register(AuthRequest registerRequest);
    UserPrincipal authenticate(AuthRequest loginRequest);
    AppUserDto updateUser(Long userId, AppUserDto userDto);
    Page<AppUserDto> getAllUsers(Pageable pageable);
}
