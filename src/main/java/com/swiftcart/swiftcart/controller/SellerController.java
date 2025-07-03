package com.swiftcart.swiftcart.controller;

import java.time.Duration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.payload.LoginRequest;
import com.swiftcart.swiftcart.payload.SellerDTO;
import com.swiftcart.swiftcart.payload.SellerRegisterRequest;
import com.swiftcart.swiftcart.security.UserDetailsImpl;
import com.swiftcart.swiftcart.security.service.JwtService;
import com.swiftcart.swiftcart.service.SellerService;
import com.swiftcart.swiftcart.service.TokenService;
import com.swiftcart.swiftcart.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerDTO> getAuthenticatedSellerProfile(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        SellerDTO sellerDTO = sellerService.getSeller(userDetailsImpl.getUser().getUserId());
        return ResponseEntity.ok(sellerDTO);
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<SellerDTO> register(@RequestBody @Valid SellerRegisterRequest registerReq) {
        sellerService.register(registerReq);
        UserDetailsImpl userDetailsImpl = userService.authenticate(modelMapper.map(registerReq, LoginRequest.class));
        SellerDTO sellerDTO = sellerService.getSeller(userDetailsImpl.getUser().getUserId());
        String jwt = jwtService.generateToken(userDetailsImpl);
        String refreshToken = tokenService.generateRefreshToken(userDetailsImpl.getUser());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofMinutes(60))
            .sameSite("Strict")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth")
            .maxAge(Duration.ofDays(30))
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            })
            .body(sellerDTO);
    }
}
