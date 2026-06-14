package com.swiftcart.swiftcart.common.security;

import java.io.IOException;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.flushBuffer();
    }
    
}
