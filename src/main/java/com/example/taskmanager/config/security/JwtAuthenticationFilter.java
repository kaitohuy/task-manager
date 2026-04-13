package com.example.taskmanager.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        //get token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        //check user and header is existed
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Validate token and extract roles
            if (!jwtUtil.isTokenExpired(jwt)) {
                List<String> authorityStrings = jwtUtil.extractAuthorities(jwt);
                Long userId = jwtUtil.extractUserId(jwt);
                List<SimpleGrantedAuthority> authorities = authorityStrings.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Create CustomUserDetails from Token info (Stateless)
                CustomUserDetails userDetails = new CustomUserDetails(userId, username, authorities, true);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 4. Set into context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //continue filter chain
        filterChain.doFilter(request, response);
    }
}
