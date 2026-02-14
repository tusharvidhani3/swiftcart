package com.swiftcart.swiftcart.features.address;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressDTO addressDTO);
    AddressDTO toDto(Address address);
    AddressSnapshot toSnapshot(Address address);
}
