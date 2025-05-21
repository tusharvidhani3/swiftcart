package com.swiftcart.swiftcart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.entity.Address;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.AccessDeniedException;
import com.swiftcart.swiftcart.payload.AddressDTO;
import com.swiftcart.swiftcart.repository.AddressRepo;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {
        Address address=modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        if(addressRepo.countByUser_UserId(user.getUserId()) == 0)
        address.setIsDefaultShipping(true);
        address=addressRepo.save(address);
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getLoggedInUserAddresses(Long userId) {
        List<Address> userAddresses = addressRepo.findAllByUser_UserId(userId);
        return userAddresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("#userId == principal.user.userId")
    public AddressDTO getAddress(Long addressId, Long userId) {
        Address address = addressRepo.findByAddressId(addressId);
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    @PreAuthorize("#userId == principal.user.userId")
    public void deleteAddress(Long addressId, Long userId) {
        addressRepo.deleteById(addressId);
    }

    @Override
    @PreAuthorize("#user.userId == principal.user.userId")
    public AddressDTO updateAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        return modelMapper.map(addressRepo.save(address), AddressDTO.class);
    }

    @Override
    public void changeDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepo.findByAddressId(addressId);
        if(address.getUser().getUserId() != userId)
        throw new AccessDeniedException("Access denied: This address does not belong to you");
        addressRepo.unsetDefaultShipping(userId);
        addressRepo.setDefaultShipping(addressId);
    }

}
