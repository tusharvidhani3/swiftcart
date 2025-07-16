package com.swiftcart.swiftcart.common.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.swiftcart.swiftcart.features.auth.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String jwtToken = jwtService.extractJwtFromCookies(request);
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String userId = null;
        try {
            userId = jwtService.extractUserId(jwtToken);
        } catch (ExpiredJwtException ex) {
            ResponseCookie cookie = ResponseCookie.from("access_token", null)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            filterChain.doFilter(request, response);
            return;
        }

        // If user is not yet authenticated in this context
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetailsImpl userDetailsImpl = this.context.getBean("userDetailsService", UserDetailsServiceImpl.class).loadUserByUserId(Long.parseLong(userId));
            if (jwtService.isTokenValid(jwtToken, userDetailsImpl)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetailsImpl,
                        null,
                        userDetailsImpl.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Mark user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
