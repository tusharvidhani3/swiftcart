package com.swiftcart.swiftcart.features.address;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.UserPrincipal;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getLoggedInUserAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(addressService.getAddressesForLoggedInUser(userPrincipal.getUser().getId()));
    }
    
    @PostMapping
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressDto addressDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(addressDto, userPrincipal.getUser()));
    }

    @GetMapping("{addressId}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        AddressDto addressDto = addressService.getAddress(addressId, userPrincipal.getUser().getId());
        return ResponseEntity.ok(addressDto);
    }

    @DeleteMapping("{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        addressService.deleteAddress(addressId, userPrincipal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody @Valid AddressDto addressDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(addressService.updateAddress(addressDto, userPrincipal.getUser()));
    }

    @PatchMapping("{addressId}/default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDto> changeDefaultAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(addressService.changeDefaultAddress(addressId, userPrincipal.getUser().getId()));
    }

    @GetMapping("default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDto> getDefaultAddress(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(addressService.getDefaultAddressForUser(userPrincipal.getUser().getId()));
    }
}
