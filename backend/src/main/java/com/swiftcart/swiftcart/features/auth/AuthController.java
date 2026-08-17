package com.swiftcart.swiftcart.features.auth;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.JwtService;
import com.swiftcart.swiftcart.common.security.SecurityProperties;
import com.swiftcart.swiftcart.features.appuser.AppUserDto;
import com.swiftcart.swiftcart.features.appuser.AppUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService userService;

    private final JwtService jwtService;

    private final RefreshTokenService tokenService;

    private final SecurityProperties securityProperties;

    @PostMapping("register")
    public ResponseEntity<AppUserDto> register(@RequestBody @Valid AuthRequest authRequest) {
        AppUserDto userDto = userService.register(authRequest);
        String jwt = jwtService.generateToken(userDto.id(), userDto.email(), userDto.mobileNumber(), userDto.role());
        String refreshToken = tokenService.generateRefreshToken(userDto.id());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(securityProperties.auth().accessToken().expirationMinutes()))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(securityProperties.auth().refreshToken().expirationDays()))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDto);
    }

    @PostMapping("login")
    public ResponseEntity<AppUserDto> login(@RequestBody @Valid AuthRequest authRequest) {
        AppUserDto userDto = userService.authenticate(authRequest);
        String jwt = jwtService.generateToken(userDto.id(), userDto.email(), userDto.mobileNumber(), userDto.role());
        String refreshToken = tokenService.generateRefreshToken(userDto.id());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(securityProperties.auth().accessToken().expirationMinutes()))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(securityProperties.auth().refreshToken().expirationDays()))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDto);
    }

    @PostMapping("google")
    public ResponseEntity<AppUserDto> googleAuth(@RequestBody GoogleAuthRequest googleAuthRequest) {
        AppUserDto userDto = userService.authenticateWithGoogle(googleAuthRequest.token());
        String jwt = jwtService.generateToken(userDto.id(), userDto.email(), userDto.mobileNumber(), userDto.role());
        String refreshToken = tokenService.generateRefreshToken(userDto.id());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(securityProperties.auth().accessToken().expirationMinutes()))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(securityProperties.auth().refreshToken().expirationDays()))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDto);
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
            .maxAge(Duration.ofMinutes(securityProperties.auth().accessToken().expirationMinutes()))
            .sameSite("None")
            .build();
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(securityProperties.auth().refreshToken().expirationDays()))
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
