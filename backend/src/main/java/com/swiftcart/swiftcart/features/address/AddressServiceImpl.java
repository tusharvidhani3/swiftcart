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
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {
        Address address=addressMapper.toEntity(addressDTO);
        address.setUser(user);
        if(addressRepo.countByUser_UserId(user.getUserId()) == 0)
        address.setIsDefaultShipping(true);
        address=addressRepo.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public List<AddressDTO> getAddressesForLoggedInUser(Long userId) {
        List<Address> userAddresses = addressRepo.findAllByUser_UserId(userId);
        return userAddresses.stream().map(address -> addressMapper.toDto(address)).collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(Long addressId, Long userId) {
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
        if(address.getIsDefaultShipping() != null && address.getIsDefaultShipping())
        throw new IllegalStateException("Cannot delete the default shipping address");
        addressRepo.deleteById(addressId);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(AddressDTO addressDTO, User user) {
        Address address = addressMapper.toEntity(addressDTO);
        Address oldAddress = addressRepo.findByAddressId(addressDTO.getAddressId()).get();
        if (!oldAddress.getUser().getUserId().equals(user.getUserId()))
            throw new AccessDeniedException("Unauthorized access to this address");
        address.setUser(user);
        address.setIsDefaultShipping(oldAddress.getIsDefaultShipping());
        address = addressRepo.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    @Transactional
    public AddressDTO changeDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepo.findByAddressId(addressId)
        .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if(address.getUser().getUserId() != userId)
        throw new AccessDeniedException("Access denied: This address does not belong to you");
        addressRepo.unsetDefaultShipping(userId);
        addressRepo.setDefaultShipping(addressId);
        return addressMapper.toDto(address);
    }

    @Override
    public AddressDTO getDefaultAddressForUser(Long userId) {
        Address address = addressRepo.findDefaultShippingAddress(userId)
        .orElseThrow(() -> new ResourceNotFoundException("No address added"));
        return addressMapper.toDto(address);
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepo.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

}
