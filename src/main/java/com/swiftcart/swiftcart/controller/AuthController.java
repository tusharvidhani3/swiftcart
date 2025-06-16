package com.swiftcart.swiftcart.controller;

import java.time.Duration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.swiftcart.swiftcart.payload.RegisterRequest;
import com.swiftcart.swiftcart.payload.UserDTO;
import com.swiftcart.swiftcart.security.UserDetailsImpl;
import com.swiftcart.swiftcart.security.service.JwtService;
import com.swiftcart.swiftcart.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegisterRequest registerReq) {
        userService.register(registerReq);
        UserDetailsImpl userDetailsImpl = userService.authenticate(modelMapper.map(registerReq, LoginRequest.class));
        UserDTO userDTO = modelMapper.map(userDetailsImpl.getUser(), UserDTO.class);
        String jwt = jwtService.generateToken(userDetailsImpl);
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(true) // set to false if you're testing on localhost without HTTPS
            .path("/")
            .maxAge(Duration.ofMinutes(15))
            .sameSite("Strict")
            .build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(userDTO);
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<UserDTO> login(@RequestBody @Valid LoginRequest loginReq) {
        UserDetailsImpl userDetailsImpl = userService.authenticate(loginReq);
        UserDTO userDTO = modelMapper.map(userDetailsImpl.getUser(), UserDTO.class);
        String jwt = jwtService.generateToken(userDetailsImpl);
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(true) // set to false if you're testing on localhost without HTTPS
            .path("/")
            .maxAge(Duration.ofMinutes(15))
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(userDTO);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getLoggedInUser(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        UserDTO userDTO = modelMapper.map(userDetailsImpl.getUser(), UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping(value = {"/logout", "/signout"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", null)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();
        
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

}
