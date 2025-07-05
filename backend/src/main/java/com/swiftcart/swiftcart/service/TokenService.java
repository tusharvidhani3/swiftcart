package com.swiftcart.swiftcart.service;

import com.swiftcart.swiftcart.entity.User;

public interface TokenService {

    public String generateRefreshToken(User user);
    public String generateAccessToken(String refreshToken);
    public void invalidateRefreshToken(String refreshToken);
    public boolean isValid(String refreshToken);
    public String rotateRefreshToken(String refreshToken);

}
