package com.swiftcart.swiftcart.common.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwtToken = jwtService.extractJwtFromCookies(request);
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.extractAllClaims(jwtToken);
        if (redisTemplate.hasKey("blacklist:" + claims.getId())) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        String mobileNumber = claims.get("mobileNumber", String.class);
        String role = claims.get("role", String.class);
        AppUserDetails userPrincipal = new AppUserDetails(userId, email, mobileNumber, Set.of(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                userPrincipal.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
