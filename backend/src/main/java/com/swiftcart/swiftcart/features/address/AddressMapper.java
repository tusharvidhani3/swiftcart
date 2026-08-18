package com.swiftcart.swiftcart.features.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressDto addressDto);
    AddressDto toDto(Address address);
    AddressSnapshot toSnapshot(Address address);
}
