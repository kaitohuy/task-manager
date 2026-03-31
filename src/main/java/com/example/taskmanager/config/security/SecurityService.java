package com.example.taskmanager.config.security;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.config.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public String getCurrentUsername() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            return "anonymous";
        }
        return context.getAuthentication().getName();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not logged in or not found: " + username));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}