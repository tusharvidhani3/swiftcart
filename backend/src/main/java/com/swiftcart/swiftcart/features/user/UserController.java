package com.swiftcart.swiftcart.features.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "userId") String sortBy) {
        Page<UserDTO> users = userService.getAllUsers(PageRequest.of(page, size, Sort.by(sortBy).ascending()));
        return ResponseEntity.ok(users);
    }
}
