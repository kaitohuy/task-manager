package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.ChangePasswordDTO;
import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.request.UpdateUserDTO;
import com.example.taskmanager.dto.response.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(CreateUserDTO request);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UpdateUserDTO request);
    void deleteUser(Long id);
    void changePassword(Long userId, ChangePasswordDTO request);
    UserDTO getByUsername(String username);
}
