package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.service.interfaces.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody CreateUserDTO request) {
        authService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return "Email verified successfully";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return "Reset password email sent";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return "Password reset successfully";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return "Verification email resent";
    }
}
