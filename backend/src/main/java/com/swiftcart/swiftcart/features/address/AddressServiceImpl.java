package com.swiftcart.swiftcart.features.address;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.common.exception.ResourceNotFoundException;
import com.swiftcart.swiftcart.features.user.User;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressDto addAddress(AddressDto addressDto, User user) {
        Address address=addressMapper.toEntity(addressDto);
        address.setUser(user);
        if(addressRepo.countByUser_UserId(user.getUserId()) == 0)
        address.setDefaultShipping(true);
        address=addressRepo.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public List<AddressDto> getAddressesForLoggedInUser(Long userId) {
        List<Address> userAddresses = addressRepo.findAllByUser_UserId(userId);
        return userAddresses.stream().map(address -> addressMapper.toDto(address)).collect(Collectors.toList());
    }

    @Override
    public AddressDto getAddress(Long addressId, Long userId) {
        Address address = addressRepo.findByAddressId(addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getUserId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this address");
        return addressMapper.toDto(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepo.findByAddressId(addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getUserId().equals(userId))
            throw new AccessDeniedException("Unauthorized access to this address");
        if(address.getDefaultShipping() != null && address.getDefaultShipping())
        throw new IllegalStateException("Cannot delete the default shipping address");
        addressRepo.deleteById(addressId);
    }

    @Override
    @Transactional
    public AddressDto updateAddress(AddressDto addressDto, User user) {
        Address address = addressMapper.toEntity(addressDto);
        Address oldAddress = addressRepo.findByAddressId(addressDto.getAddressId()).get();
        if (!oldAddress.getUser().getUserId().equals(user.getUserId()))
            throw new AccessDeniedException("Unauthorized access to this address");
        address.setUser(user);
        address.setDefaultShipping(oldAddress.getDefaultShipping());
        address = addressRepo.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    @Transactional
    public AddressDto changeDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepo.findByAddressId(addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if(address.getUser().getUserId() != userId)
        throw new AccessDeniedException("Access denied: This address does not belong to you");
        addressRepo.unsetDefaultShipping(userId);
        addressRepo.setDefaultShipping(addressId);
        return addressMapper.toDto(address);
    }

    @Override
    public AddressDto getDefaultAddressForUser(Long userId) {
        Address address = addressRepo.findDefaultShippingAddress(userId)
        .orElseThrow(() -> new ResourceNotFoundException("No address added"));
        return addressMapper.toDto(address);
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

}
