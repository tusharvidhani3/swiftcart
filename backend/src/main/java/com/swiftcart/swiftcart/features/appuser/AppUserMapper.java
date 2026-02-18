package com.swiftcart.swiftcart.features.appuser;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.swiftcart.swiftcart.features.auth.AuthRequest;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppUserMapper {
    
    @Mapping(target = "role", ignore = true)
    AppUser toEntity(AppUserDto userDto);

    @Mapping(source = "role.name", target = "role")
    AppUserDto toDto(AppUser user);

    @Mapping(target = "id", ignore = true)
    void update(AppUserDto userDto, @MappingTarget AppUser user);
    AppUser toEntity(AuthRequest authRequest);
}
