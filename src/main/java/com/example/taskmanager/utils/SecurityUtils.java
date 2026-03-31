package com.example.taskmanager.utils;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class SecurityUtils {

    private static UserRepository userRepository;

    public static String getCurrentUsername() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            return "anonymous";
        }
        return context.getAuthentication().getName();
    }

    public static User getCurrentUser() {
        String username = getCurrentUsername();
        if ("anonymous".equals(username)) return null;
        if (userRepository == null) {
            throw new IllegalStateException("UserRepository not initialized in SecurityUtils");
        }
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}