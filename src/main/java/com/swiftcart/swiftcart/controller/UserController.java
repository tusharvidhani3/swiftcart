package com.swiftcart.swiftcart.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.payload.UserDTO;
import com.swiftcart.swiftcart.security.UserDetailsImpl;
import com.swiftcart.swiftcart.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getLoggedInUser(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        UserDTO userDTO = modelMapper.map(userDetailsImpl.getUser(), UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestBody UserDTO userDTO) {
        userDTO.setUserId(userDetailsImpl.getUser().getUserId());
        userService.updateUser(userDTO);
        return ResponseEntity.ok().build();
    }
}
