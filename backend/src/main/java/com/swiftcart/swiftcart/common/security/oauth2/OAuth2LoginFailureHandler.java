package com.swiftcart.swiftcart.common.security.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.frontend.auth.failure-path}")
    private String authFailurePath;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage = exception.getMessage();
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + authFailurePath).queryParam("error", errorMessage).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
