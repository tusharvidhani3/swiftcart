package com.swiftcart.swiftcart.features.address;

import java.util.List;

import com.swiftcart.swiftcart.features.user.User;

public interface AddressService {

    public AddressDTO addAddress(AddressDTO addressDTO, User user);
    public List<AddressDTO> getAddressesForLoggedInUser(Long userId);
    public AddressDTO getAddress(Long addressId, Long userId);
    public void deleteAddress(Long addressId, Long userId);
    public AddressDTO updateAddress(AddressDTO addressDTO, User user);
    public AddressDTO changeDefaultAddress(Long addressId, Long userId);
    public AddressDTO getDefaultAddressForUser(Long userId);
    Address getAddressById(Long addressId);
}
