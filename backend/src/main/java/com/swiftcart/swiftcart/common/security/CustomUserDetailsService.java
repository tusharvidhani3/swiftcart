package com.swiftcart.swiftcart.common.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.features.appuser.AppUser;
import com.swiftcart.swiftcart.features.appuser.AppUserRepository;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserPrincipal(user.getId(), user.getEmail(), user.getMobileNumber(), Set.of(new SimpleGrantedAuthority(user.getRole().getName())));
    }

}
