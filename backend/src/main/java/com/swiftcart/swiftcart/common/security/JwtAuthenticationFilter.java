package com.swiftcart.swiftcart.common.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwtToken = jwtService.extractJwtFromCookies(request);
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Optional<String> userIdOpt = jwtService.extractUserId(jwtToken);

        userIdOpt.ifPresent(userId -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserPrincipal userPrincipal = this.context.getBean("userDetailsService", CustomUserDetailsService.class).loadUserByUserId(Long.parseLong(userId));
                if (userId.equals(userPrincipal.getUser().getId().toString())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        });
        
        filterChain.doFilter(request, response);
    }

}
