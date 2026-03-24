package com.example.taskmanager.dto.request;

import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CreateUserDTO {
    private Set<Role> roles;
    private String fullName;
    private String address;
    private Gender gender;
    private LocalDate dob;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Email(message = "Email invalid")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

}
