package com.swiftcart.swiftcart.features.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swiftcart.swiftcart.features.auth.AuthRequest;

@Service
public class AppUserService {

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

    @Transactional
    public void register(AuthRequest registerRequest) {
        AppUser user = userMapper.toEntity(registerRequest);
        Role role = roleRepo.findByName("ROLE_CUSTOMER");
        user.setRole(role);
        user.setPassword(encoder.encode(registerRequest.password()));
        userRepo.save(user);
    }

    public AppUserDto authenticate(AuthRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        AppUser user = userRepo.findByEmail(loginRequest.email()).orElseThrow(() -> new UsernameNotFoundException("User does not exists with this email"));
        return userMapper.toDto(user);
    }

    @Transactional
    public AppUserDto updateUser(Long userId, AppUserDto userDto) {
        AppUser user = userRepo.findById(userId).get();
        userMapper.update(userDto, user);
        user = userRepo.save(user);
        return userDto;
    }

    public Page<AppUserDto> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(user -> userMapper.toDto(user));
    }

    // public CustomerStats getCustomerStats() {
    //     CustomerStats customerStats = userRepo.getCustomerStats(LocalDate.now().minusMonths(1));
    //     return customerStats;
    // }

}
