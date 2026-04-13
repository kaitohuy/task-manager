package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.dto.response.ApiResponse;
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
    public ApiResponse register(@Valid @RequestBody CreateUserDTO request) {
        authService.register(request);
        return new ApiResponse("User registered successfully");
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
    public ApiResponse verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return new ApiResponse("Email verified successfully");
    }

    @PostMapping("/forgot-password")
    public ApiResponse forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return new ApiResponse("Reset password email sent");
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return new ApiResponse("Password reset successfully");
    }

    @PostMapping("/resend-verification")
    public ApiResponse resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return new ApiResponse("Verification email resent");
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(@RequestParam String mfaToken, @RequestParam String code, HttpServletResponse response) {
        return authService.verifyOtp(mfaToken, code, response);
    }

    @GetMapping("/mfa/setup")
    public ApiResponse setupMfa(@RequestParam String username) {
        String qrCodeUri = authService.setupMfa(username);
        return new ApiResponse(qrCodeUri); // Returning QR URI in the message field
    }

    @PostMapping("/mfa/enable")
    public ApiResponse enableMfa(@RequestParam String username, @RequestParam String code) {
        authService.enableMfa(username, code);
        return new ApiResponse("2FA enabled successfully");
    }

    @PostMapping("/mfa/disable")
    public ApiResponse disableMfa(@RequestParam String username) {
        authService.disableMfa(username);
        return new ApiResponse("2FA disabled successfully");
    }
}
