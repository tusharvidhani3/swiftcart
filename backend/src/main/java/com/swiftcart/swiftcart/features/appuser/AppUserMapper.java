package com.swiftcart.swiftcart.features.appuser;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.swiftcart.swiftcart.features.auth.AuthRequest;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    
    @Mapping(target = "role", ignore = true)
    AppUser toEntity(AppUserDto userDto);

    @Mapping(target = "role", ignore = true)
    AppUserDto toDto(AppUser user);

    @Mapping(target = "role", ignore = true)
    void update(AppUser user, @MappingTarget AppUserDto userDto);
    AppUser toEntity(AuthRequest authRequest);
}
