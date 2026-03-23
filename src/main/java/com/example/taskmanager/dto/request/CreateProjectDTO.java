package com.example.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectDTO {

    @NotBlank(message = "name cannot be empty")
    private String name;

    @Length(max = 500, message = "description too long")
    private String description;

    @NotNull(message = "user's ID is required")
    private Long createdById;
}
