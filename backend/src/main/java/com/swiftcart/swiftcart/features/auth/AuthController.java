package com.swiftcart.swiftcart.features.auth;

import java.time.Duration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;
import com.swiftcart.swiftcart.features.user.User;
import com.swiftcart.swiftcart.features.user.UserDTO;
import com.swiftcart.swiftcart.features.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService tokenService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<UserDTO> register(@RequestBody @Valid LoginRequest registerReq) {
        userService.register(registerReq);
        UserDetailsImpl userDetailsImpl = userService.authenticate(registerReq);
        User user = userDetailsImpl.getUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setRole(user.getRole().getName());
        String jwt = jwtService.generateToken(user.getUserId(), user.getRole().getName());
        String refreshToken = tokenService.generateRefreshToken(userDetailsImpl.getUser());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(60))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(30))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDTO);
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<UserDTO> login(@RequestBody @Valid LoginRequest loginReq) {
        UserDetailsImpl userDetailsImpl = userService.authenticate(loginReq);
        User user = userDetailsImpl.getUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setRole(user.getRole().getName());
        String jwt = jwtService.generateToken(user.getUserId(), user.getRole().getName());
        String refreshToken = tokenService.generateRefreshToken(userDetailsImpl.getUser());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(60))
            .sameSite("None")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(30))
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(userDTO);
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

    @PostMapping("/refresh-token")
    public ResponseEntity<Void> refreshAccessToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null || !tokenService.isValid(refreshToken))
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String newRefreshToken = tokenService.rotateRefreshToken(refreshToken);
        String newAccessToken = tokenService.generateAccessToken(newRefreshToken);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(60))
            .sameSite("None")
            .build();
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(30))
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
