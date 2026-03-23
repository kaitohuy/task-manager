package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.ChangePasswordDTO;
import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.UpdateUserDTO;
import com.example.taskmanager.dto.response.UserDTO;
import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    UserDTO createUser(CreateUserDTO request);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UpdateUserDTO request);
    void deleteUser(Long id);
    void changePassword(Long userId, ChangePasswordDTO request);
    UserDTO getByUsername(String username);
    Page<UserDTO> getAllUser(Pageable pageable);
    Page<UserDTO> searchAdvanceUsers(String keyword, Gender gender, Role role, Pageable pageable);
}
