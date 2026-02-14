package com.swiftcart.swiftcart.features.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.swiftcart.swiftcart.features.auth.LoginRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "role", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "role", ignore = true)
    UserDTO toDto(User user);

    @Mapping(target = "role", ignore = true)
    void update(User user, @MappingTarget UserDTO userDTO);
    User toEntity(LoginRequest registerRequest);
}
