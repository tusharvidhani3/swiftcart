package com.swiftcart.swiftcart.features.appuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swiftcart.swiftcart.features.auth.AuthRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserMapper userMapper;

    private final AppUserRepository userRepo;

    private final RoleRepository roleRepo;

    private final PasswordEncoder encoder;

    private final AuthenticationManager authenticationManager;

    private final FirebaseAuth firebaseAuth;

    @Transactional
    public AppUserDto register(AuthRequest registerRequest) {
        AppUser user = userMapper.toEntity(registerRequest);
        Role role = roleRepo.findByName("ROLE_CUSTOMER");
        user.setRole(role);
        user.setPassword(encoder.encode(registerRequest.password()));
        user = userRepo.save(user);
        return userMapper.toDto(user);
    }

    public AppUserDto authenticate(AuthRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        AppUser user = userRepo.findByEmail(loginRequest.email()).orElseThrow(() -> new UsernameNotFoundException("User does not exists with this email"));
        return userMapper.toDto(user);
    }

    @Transactional
    public AppUserDto authenticateWithGoogle(String token) {
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(token);

            if (!decodedToken.isEmailVerified())
                throw new IllegalArgumentException("Google email is not verified");
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Failed to verify Google Firebase token", e);
        }
        String email = decodedToken.getEmail();
        if(email == null || email.isBlank())
            throw new BadCredentialsException("Email claim missing from token");

        AppUser user = userRepo.findByEmail(email).orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setEmail(email);
            Role role = roleRepo.findByName("ROLE_CUSTOMER");
            newUser.setRole(role);
            return userRepo.save(newUser);
        });
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
