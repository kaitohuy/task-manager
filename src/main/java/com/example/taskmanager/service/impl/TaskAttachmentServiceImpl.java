package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.request.CreateAttachmentDTO;
import com.example.taskmanager.dto.response.AttachmentResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskAttachment;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.enums.AttachmentType;
import com.example.taskmanager.mapper.AttachmentMapper;
import com.example.taskmanager.repository.TaskAttachmentRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.interfaces.TaskAttachmentService;
import com.example.taskmanager.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AttachmentMapper attachmentMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponseDTO> getAttachments(Long taskId) {
        return attachmentRepository.findByTaskIdOrderByUploadedAtDesc(taskId)
                .stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AttachmentResponseDTO addAttachment(Long taskId, CreateAttachmentDTO request, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Không tìm thấy Task"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        TaskAttachment attachment = attachmentMapper.toEntity(request);
        attachment.setTask(task);
        attachment.setUser(user);

        TaskAttachment savedAttachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(savedAttachment);
    }

    @Override
    @Transactional
    public AttachmentResponseDTO uploadFile(Long taskId, MultipartFile file, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Không tìm thấy Task"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        String fileUrl = cloudinaryService.uploadFile(file);
        String fileName = file.getOriginalFilename();
        AttachmentType type = determineFileType(file.getContentType());
        TaskAttachment attachment = TaskAttachment.builder()
                .task(task)
                .user(user)
                .name(fileName)
                .url(fileUrl)
                .type(type)
                .build();

        TaskAttachment savedAttachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(savedAttachment);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId, Long userId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new RuntimeException("File not found"));

        if (!attachment.getUser().getId().equals(userId)) {
            throw new RuntimeException("you don't have permission to delete this file");
        }

        String url = attachment.getUrl();
        if (url != null && url.contains("res.cloudinary.com")) {
            cloudinaryService.deleteFile(url);
        }

        attachmentRepository.delete(attachment);
    }

    private AttachmentType determineFileType(String contentType) {
        if (contentType == null) return AttachmentType.OTHER;

        if (contentType.startsWith("image/")) {
            return AttachmentType.IMAGE;
        } else if (contentType.equals("application/pdf")) {
            return AttachmentType.PDF;
        } else if (contentType.contains("msword") || contentType.contains("wordprocessingml")) {
            return AttachmentType.WORD;
        } else if (contentType.contains("ms-excel") || contentType.contains("spreadsheetml")) {
            return AttachmentType.EXCEL;
        }
        return AttachmentType.OTHER;
    }
}