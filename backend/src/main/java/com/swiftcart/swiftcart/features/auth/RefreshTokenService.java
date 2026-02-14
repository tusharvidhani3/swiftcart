package com.swiftcart.swiftcart.features.auth;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface RefreshTokenService {

    public String generateRefreshToken(AppUser user);
    public String generateAccessToken(String refreshToken);
    public void invalidateRefreshToken(String refreshToken);
    public boolean isValid(String refreshToken);
    public String rotateRefreshToken(String refreshToken);

}
