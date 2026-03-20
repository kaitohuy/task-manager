package com.example.taskmanager.dto.response;

import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private Set<Role> roles;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private String address;
    private Gender gender;
    private LocalDate dob;
}
