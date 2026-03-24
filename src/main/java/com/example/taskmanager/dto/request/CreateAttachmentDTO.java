package com.example.taskmanager.dto.request;

import com.example.taskmanager.enums.AttachmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttachmentDTO {

    @NotBlank(message = "name file/url cannot empty!")
    private String name;

    @NotBlank(message = "url cannot empty")
    private String url;

    @NotNull(message = "Please choose type for attachment!")
    private AttachmentType type;
}