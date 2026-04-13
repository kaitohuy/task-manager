package com.example.taskmanager.service.impl;

import com.example.taskmanager.config.exception.BadRequestException;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import com.example.taskmanager.config.security.CustomUserDetailsService;
import com.example.taskmanager.config.security.JwtUtil;
import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.entity.PasswordResetToken;
import com.example.taskmanager.entity.RefreshToken;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.VerificationToken;
import com.example.taskmanager.repository.PasswordResetTokenRepository;
import com.example.taskmanager.repository.RefreshTokenRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.repository.VerificationTokenRepository;
import com.example.taskmanager.service.interfaces.AuthService;
import com.example.taskmanager.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MfaService mfaService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new BadRequestException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentifier());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isMfaEnabled()) {
            // Return intermediate response for MFA
            String mfaToken = jwtUtil.generateToken(userDetails); // Use normal token for now but identify it's for MFA
            return AuthResponse.builder()
                    .username(user.getUsername())
                    .mfaRequired(true)
                    .mfaToken(mfaToken)
                    .build();
        }

        String token = jwtUtil.generateToken(userDetails);
        
        // Generate and save Refresh Token
        String refreshTokenString = tokenService.createRefreshToken(userDetails.getUsername());
        
        // Add Refresh Token to Cookie
        tokenService.addRefreshTokenCookie(response, refreshTokenString);

        return AuthResponse.builder()
                .username(userDetails.getUsername())
                .token(token)
                .mfaRequired(false)
                .build();
    }

    @Override
    @Transactional
    public void register(CreateUserDTO request) {
        userService.createUser(request);
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Set enabled to false for new users
        user.setEnabled(false);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        
        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(HttpServletRequest request) {
        String refreshTokenString = tokenService.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token was expired. Please login again");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .username(userDetails.getUsername())
                .token(newAccessToken)
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Mã xác thực không hợp lệ"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Mã xác thực đã hết hạn");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email này"));

        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.deleteByUser(user);
        
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Mã đặt lại mật khẩu không hợp lệ"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Mã đặt lại mật khẩu đã hết hạn");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.isVerified()) {
            throw new BadRequestException("Tài khoản đã được xác thực");
        }

        verificationTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        
        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        tokenService.getRefreshTokenFromCookie(request).ifPresent(token -> {
            refreshTokenRepository.deleteByToken(token);
            tokenService.clearRefreshTokenCookie(response);
        });
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(String mfaToken, String code, HttpServletResponse response) {
        // Extract username from temporary token
        String username = jwtUtil.extractUsername(mfaToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!mfaService.verifyCode(code, user.getMfaSecret())) {
            throw new BadRequestException("Mã OTP không hợp lệ");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);
        String refreshTokenString = tokenService.createRefreshToken(username);
        tokenService.addRefreshTokenCookie(response, refreshTokenString);

        return AuthResponse.builder()
                .username(username)
                .token(token)
                .mfaRequired(false)
                .build();
    }

    @Override
    @Transactional
    public String setupMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getMfaSecret() == null) {
            user.setMfaSecret(mfaService.generateSecretKey());
            userRepository.save(user);
        }

        try {
            return mfaService.generateQrCodeUri(user.getMfaSecret(), user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi sinh mã QR: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void enableMfa(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (mfaService.verifyCode(code, user.getMfaSecret())) {
            user.setMfaEnabled(true);
            userRepository.save(user);
        } else {
            throw new BadRequestException("Mã xác thực không chính xác");
        }
    }

    @Override
    @Transactional
    public void disableMfa(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
    }
}
