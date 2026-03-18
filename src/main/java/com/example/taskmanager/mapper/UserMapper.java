package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.CreateUserDTO;
import com.example.taskmanager.dto.response.UserDTO;
import com.example.taskmanager.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(CreateUserDTO request);

}
