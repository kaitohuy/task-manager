package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    void register(CreateUserDTO request);
}
