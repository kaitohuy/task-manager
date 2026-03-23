package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.CreateAttachmentDTO;
import com.example.taskmanager.dto.response.AttachmentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskAttachmentService {

    List<AttachmentResponseDTO> getAttachments(Long taskId);
    AttachmentResponseDTO addAttachment(Long taskId, CreateAttachmentDTO request, Long userId);
    void deleteAttachment(Long attachmentId, Long userId);
    AttachmentResponseDTO uploadFile(Long taskId, MultipartFile file, Long userId);
}