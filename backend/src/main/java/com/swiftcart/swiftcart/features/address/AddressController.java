package com.swiftcart.swiftcart.features.address;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.AppUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/addresses")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER')")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getLoggedInUserAddresses(@AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.ok(addressService.getAddressesForLoggedInUser(userPrincipal.getUserId()));
    }
    
    @PostMapping
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressDto addressDto, @AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(addressDto, userPrincipal.getUserId()));
    }

    @GetMapping("{addressId}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long addressId, @AuthenticationPrincipal AppUserDetails userPrincipal) {
        AddressDto addressDto = addressService.getAddress(addressId, userPrincipal.getUserId());
        return ResponseEntity.ok(addressDto);
    }

    @DeleteMapping("{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal AppUserDetails userPrincipal) {
        addressService.deleteAddress(addressId, userPrincipal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping()
    public ResponseEntity<AddressDto> updateAddress(@RequestBody @Valid AddressDto addressDto, @AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.ok(addressService.updateAddress(addressDto, userPrincipal.getUserId()));
    }

    @PatchMapping("{addressId}/default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDto> changeDefaultAddress(@PathVariable Long addressId, @AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.ok(addressService.changeDefaultAddress(addressId, userPrincipal.getUserId()));
    }

    @GetMapping("default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDto> getDefaultAddress(@AuthenticationPrincipal AppUserDetails userPrincipal) {
        return ResponseEntity.ok(addressService.getDefaultAddressForUser(userPrincipal.getUserId()));
    }
}
