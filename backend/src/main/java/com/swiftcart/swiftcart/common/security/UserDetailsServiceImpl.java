package com.swiftcart.swiftcart.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.user.User;
import com.swiftcart.swiftcart.features.user.UserRepo;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findByMobileNumberWithRole(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }

    public UserDetailsImpl loadUserByUserId(Long userId) {
        User user = userRepo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }

}
