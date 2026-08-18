package com.swiftcart.swiftcart.features.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.AppUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService userService;

    @GetMapping("me")
    public ResponseEntity<AppUserDto> getLoggedInUser(@AuthenticationPrincipal AppUserDetails userPrincipal) {
        AppUserDto userDto = new AppUserDto(userPrincipal.getUserId(), null, null, userPrincipal.getMobileNumber(), userPrincipal.getMobileNumber(), userPrincipal.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping
    public ResponseEntity<AppUserDto> updateUserInfo(@AuthenticationPrincipal AppUserDetails userPrincipal, @RequestBody AppUserDto userDto) {
        AppUserDto updatedUserDto = userService.updateUser(userPrincipal.getUserId(), userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<AppUserDto>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, PagedResourcesAssembler<AppUserDto> assembler) {
        Page<AppUserDto> users = userService.getAllUsers(PageRequest.of(page, size, Sort.by(sortBy).ascending()));
        return ResponseEntity.ok(assembler.toModel(users));
    }
}
