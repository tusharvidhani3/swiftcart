package com.swiftcart.swiftcart.common.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

public class AppUserDetails implements UserDetails {

    @Getter
    private Long userId;
    @Getter
    private String email;
    @Getter
    private String mobileNumber;
    private Set<GrantedAuthority> authorities;

    public AppUserDetails(Long userId, String email, String mobileNumber, Set<GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }
}