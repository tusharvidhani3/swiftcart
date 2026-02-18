package com.swiftcart.swiftcart.features.address;

import java.util.List;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface AddressService {
    AddressDto addAddress(AddressDto addressDto, AppUser user);
    List<AddressDto> getAddressesForLoggedInUser(Long userId);
    AddressDto getAddress(Long addressId, Long userId);
    void deleteAddress(Long addressId, Long userId);
    AddressDto updateAddress(AddressDto addressDto, AppUser user);
    AddressDto changeDefaultAddress(Long addressId, Long userId);
    AddressDto getDefaultAddressForUser(Long userId);
    Address getAddressById(Long addressId);
}
