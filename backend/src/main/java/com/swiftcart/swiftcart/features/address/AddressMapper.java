package com.swiftcart.swiftcart.features.address;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDto addressDto);
    AddressDto toDto(Address address);
    AddressSnapshot toSnapshot(Address address);
}
