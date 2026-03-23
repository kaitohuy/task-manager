package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.ChangePasswordDTO;
import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.SearchTaskDTO;
import com.example.taskmanager.dto.request.UpdateUserDTO;
import com.example.taskmanager.dto.response.TaskDTO;
import com.example.taskmanager.dto.response.UserDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.Role;
import com.example.taskmanager.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getByUsername(userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUser(pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO request) {
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDTO request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-advanced")
    public ResponseEntity<Page<UserDTO>> searchUser(String keyword, Gender gender, Role role, Pageable pageable) {
        return ResponseEntity.ok(userService.searchAdvanceUsers(keyword, gender, role, pageable));
    }
}
