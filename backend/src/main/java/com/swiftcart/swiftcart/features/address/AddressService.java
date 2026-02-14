package com.swiftcart.swiftcart.features.address;

import java.util.List;

import com.swiftcart.swiftcart.features.appuser.AppUser;

public interface AddressService {

    public AddressDto addAddress(AddressDto addressDto, AppUser user);
    public List<AddressDto> getAddressesForLoggedInUser(Long userId);
    public AddressDto getAddress(Long addressId, Long userId);
    public void deleteAddress(Long addressId, Long userId);
    public AddressDto updateAddress(AddressDto addressDto, AppUser user);
    public AddressDto changeDefaultAddress(Long addressId, Long userId);
    public AddressDto getDefaultAddressForUser(Long userId);
    Address getAddressById(Long addressId);
}
