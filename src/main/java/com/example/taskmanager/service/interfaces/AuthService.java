package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.response.AuthResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    void register(CreateUserDTO request);
    AuthResponse refreshToken(HttpServletRequest request);
    void logout(HttpServletRequest request, HttpServletResponse response);
    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void resendVerificationEmail(String email);
}
