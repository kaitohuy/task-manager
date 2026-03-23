package com.example.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO {

    @NotBlank(message = "content cannot empty!!!")
    private String content;
    private Long parentCommentId;
}