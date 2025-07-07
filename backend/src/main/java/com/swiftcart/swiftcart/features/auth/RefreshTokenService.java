package com.swiftcart.swiftcart.features.auth;

import com.swiftcart.swiftcart.features.user.User;

public interface RefreshTokenService {

    public String generateRefreshToken(User user);
    public String generateAccessToken(String refreshToken);
    public void invalidateRefreshToken(String refreshToken);
    public boolean isValid(String refreshToken);
    public String rotateRefreshToken(String refreshToken);

}
