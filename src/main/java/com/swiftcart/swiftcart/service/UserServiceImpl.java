package com.swiftcart.swiftcart.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.entity.Role;
import com.swiftcart.swiftcart.entity.User;
import com.swiftcart.swiftcart.exception.BadRequestException;
import com.swiftcart.swiftcart.payload.LoginRequest;
import com.swiftcart.swiftcart.payload.RegisterRequest;
import com.swiftcart.swiftcart.payload.UserDTO;
import com.swiftcart.swiftcart.repository.RoleRepo;
import com.swiftcart.swiftcart.repository.UserRepo;
import com.swiftcart.swiftcart.security.UserDetailsImpl;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        if(registerRequest.getRole().equals("ROLE_SELLER") && registerRequest.getEmail() == null)
        throw new BadRequestException("Email is required for seller registration");
        User user = modelMapper.map(registerRequest, User.class);
        Role role=roleRepo.getRoleByName(registerRequest.getRole());
        user.getRoles().add(role);
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        userRepo.save(user);
    }

    @Override
    public UserDetailsImpl authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getMobileNumber(), loginRequest.getPassword()));
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        return userDetailsImpl;
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        userRepo.save(modelMapper.map(userDTO, User.class));
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(user -> modelMapper.map(user, UserDTO.class));
    }

}
