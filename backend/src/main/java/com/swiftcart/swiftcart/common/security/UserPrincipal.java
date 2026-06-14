package com.swiftcart.swiftcart.common.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import lombok.Getter;

public class UserPrincipal implements UserDetails, OidcUser {

    @Getter
    private Long userId;
    @Getter
    private String email;
    @Getter
    private String mobileNumber;
    private Set<GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    public UserPrincipal(Long userId, String email, String mobileNumber, Set<GrantedAuthority> authorities, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        this.userId = userId;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.authorities = authorities;
        this.attributes = attributes;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    public UserPrincipal(Long userId, String email, String mobileNumber, Set<GrantedAuthority> authorities) {
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

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public Map<String, Object> getClaims() {
        return idToken.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
