package com.swiftcart.swiftcart.common.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.swiftcart.swiftcart.common.security.UserPrincipal;
import com.swiftcart.swiftcart.features.appuser.AppUser;
import com.swiftcart.swiftcart.features.appuser.AppUserRepository;
import com.swiftcart.swiftcart.features.appuser.RoleRepository;
import com.swiftcart.swiftcart.features.appuser.UserProvider;
import com.swiftcart.swiftcart.features.appuser.UserProviderRepository;
import com.swiftcart.swiftcart.features.auth.AuthProvider;

import jakarta.transaction.Transactional;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private AppUserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserProviderRepository userProviderRepo;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        return processOidcUser(userRequest, oidcUser);
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        String email = oidcUser.getAttribute("email");
        String regId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(regId.toUpperCase());
        AppUser user = userRepo.findByEmail(email).orElseGet(() -> createNewUser(oidcUser, provider));
        return new UserPrincipal(user, oidcUser.getAttributes(), userRequest.getIdToken(), oidcUser.getUserInfo());
    }

    @Transactional
    private AppUser createNewUser(OidcUser oidcUser, AuthProvider authProvider) {
        AppUser user = new AppUser();
        user.setEmail(oidcUser.getAttribute("email"));
        user.setFirstName(oidcUser.getAttribute("given_name"));
        user.setLastName(oidcUser.getAttribute("family_name"));
        user.setRole(roleRepo.findByName("ROLE_CUSTOMER"));
        user = userRepo.saveAndFlush(user);

        UserProvider userProvider = new UserProvider();
        userProvider.setProvider(authProvider);
        userProvider.setProviderId(oidcUser.getAttribute("sub"));
        userProvider.setUser(user);
        userProviderRepo.save(userProvider);

        return user;
    }
}