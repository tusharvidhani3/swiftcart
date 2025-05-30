package com.swiftcart.swiftcart.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.repository.UserRepo;
import com.swiftcart.swiftcart.security.UserDetailsImpl;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user=userRepo.findByMobileNumberWithRoles(username);
        if(user.isEmpty())
        throw new UsernameNotFoundException("User not found");
        return new UserDetailsImpl(user.get());
    }

}
