package com.swiftcart.swiftcart.common.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public class UserDetailsImpl implements UserDetails {

    private AppUser user;

    public UserDetailsImpl(AppUser user) {
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getId().toString();
    }

    public AppUser getUser() {
        return this.user;
    }
}
