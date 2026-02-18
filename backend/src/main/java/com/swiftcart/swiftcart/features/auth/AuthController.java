package com.swiftcart.swiftcart.features.auth;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.UserPrincipal;
import com.swiftcart.swiftcart.features.appuser.AppUser;
import com.swiftcart.swiftcart.features.appuser.AppUserDto;
import com.swiftcart.swiftcart.features.appuser.AppUserMapper;
import com.swiftcart.swiftcart.features.appuser.AppUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AppUserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService tokenService;

    @Autowired
    private AppUserMapper userMapper;

    @Value("${app.security.auth.access-token.expiration-minutes}")
    private long accessTokenExpirationMinutes;

    @Value("${app.security.auth.refresh-token.expiration-days}")
    private long refreshTokenExpirationDays;

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<AppUserDto> register(@RequestBody @Valid AuthRequest authRequest) {
        userService.register(authRequest);
        UserPrincipal userPrincipal = userService.authenticate(authRequest);
        AppUser user = userPrincipal.getUser();
        AppUserDto userDto = userMapper.toDto(user);
        String jwt = jwtService.generateToken(user.getId(), user.getRole().getName());
        String refreshToken = tokenService.generateRefreshToken(userPrincipal.getUser());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(accessTokenExpirationMinutes))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(refreshTokenExpirationDays))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDto);
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<AppUserDto> login(@RequestBody @Valid AuthRequest authRequest) {
        UserPrincipal userPrincipal = userService.authenticate(authRequest);
        AppUser user = userPrincipal.getUser();
        AppUserDto userDto = userMapper.toDto(user);
        String jwt = jwtService.generateToken(user.getId(), user.getRole().getName());
        String refreshToken = tokenService.generateRefreshToken(userPrincipal.getUser());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(accessTokenExpirationMinutes))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(refreshTokenExpirationDays))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDto);
    }

    @PostMapping(value = {"/logout", "/signout"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if(refreshToken != null)
        tokenService.invalidateRefreshToken(refreshToken);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", null)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", null)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(0)
            .sameSite("None")
            .build();
        
        return ResponseEntity
        .noContent()
        .headers(headers -> {
            headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
            headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        })
        .build();
    }

    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppUserDto> getAuthenticatedUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        AppUserDto userDto = userMapper.toDto(userPrincipal.getUser());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("refresh-token")
    public ResponseEntity<Void> refreshAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || !tokenService.isValid(refreshToken))
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String newRefreshToken = tokenService.rotateRefreshToken(refreshToken);
        String newAccessToken = tokenService.generateAccessToken(newRefreshToken);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(accessTokenExpirationMinutes))
            .sameSite("None")
            .build();
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(refreshTokenExpirationDays))
            .sameSite("None")
            .build();
        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .build();
    }

}
