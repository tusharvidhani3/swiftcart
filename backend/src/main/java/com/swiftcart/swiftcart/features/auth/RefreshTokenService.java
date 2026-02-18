package com.swiftcart.swiftcart.features.auth;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface RefreshTokenService {
    String generateRefreshToken(AppUser user);
    String generateAccessToken(String refreshToken);
    void invalidateRefreshToken(String refreshToken);
    boolean isValid(String refreshToken);
    String rotateRefreshToken(String refreshToken);

}
