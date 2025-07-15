package com.swiftcart.swiftcart.features.user;

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

import com.swiftcart.swiftcart.common.security.UserDetailsImpl;
import com.swiftcart.swiftcart.features.auth.LoginRequest;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void register(LoginRequest registerRequest) {
        User user = modelMapper.map(registerRequest, User.class);
        Role role=roleRepo.getRoleByName("ROLE_CUSTOMER");
        user.setRole(role);
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
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepo.findByUserId(userDTO.getUserId()).get();
        modelMapper.map(userDTO, user);
        userRepo.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(user -> modelMapper.map(user, UserDTO.class));
    }

}
