package com.swiftcart.swiftcart.features.address;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/addresses")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER')")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getLoggedInUserAddresses(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return ResponseEntity.ok(addressService.getAddressesForLoggedInUser(userDetailsImpl.getUser().getId()));
    }
    
    @PostMapping
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressDto addressDto, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return new ResponseEntity<>(addressService.addAddress(addressDto, userDetailsImpl.getUser()), HttpStatus.CREATED);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        AddressDto addressDto = addressService.getAddress(addressId, userDetailsImpl.getUser().getId());
        return new ResponseEntity<>(addressDto, HttpStatus.OK);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        addressService.deleteAddress(addressId, userDetailsImpl.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody @Valid AddressDto addressDto, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return new ResponseEntity<>(addressService.updateAddress(addressDto, userDetailsImpl.getUser()), HttpStatus.OK);
    }

    @PutMapping("/{addressId}/default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDto> changeDefaultAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return ResponseEntity.ok(addressService.changeDefaultAddress(addressId, userDetailsImpl.getUser().getId()));
    }

    @GetMapping("/default")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDto> getDefaultAddress(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return ResponseEntity.ok(addressService.getDefaultAddressForUser(userDetailsImpl.getUser().getId()));
    }
}
