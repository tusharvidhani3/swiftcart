package com.swiftcart.swiftcart.features.appuser;

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
import com.swiftcart.swiftcart.features.auth.AuthRequest;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserMapper userMapper;

    @Autowired
    private AppUserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void register(AuthRequest registerRequest) {
        AppUser user = userMapper.toEntity(registerRequest);
        Role role=roleRepo.findByName("ROLE_CUSTOMER");
        user.setRole(role);
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        userRepo.save(user);
    }

    @Override
    public UserDetailsImpl authenticate(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getMobileNumber(), loginRequest.getPassword()));
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        return userDetailsImpl;
    }

    @Override
    @Transactional
    public AppUserDto updateUser(AppUserDto userDto) {
        AppUser user = userRepo.findByUserId(userDto.getUserId()).get();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setMobileNumber(userDto.getMobileNumber());
        user.setEmail(userDto.getEmail());
        user = userRepo.save(user);
        userMapper.update(user, userDto);
        userDto.setRole(user.getRole().getName());
        return userDto;
    }

    @Override
    public Page<AppUserDto> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(user -> userMapper.toDto(user));
    }

}
