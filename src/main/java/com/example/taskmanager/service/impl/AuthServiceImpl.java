package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.security.CustomUserDetailsService;
import com.example.taskmanager.config.security.JwtUtil;
import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.service.interfaces.AuthService;
import com.example.taskmanager.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentifier());
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(
                token,
                userDetails.getUsername()
        );
    }

    @Override
    public void register(CreateUserDTO request) {
        userService.createUser(request);
    }
}
