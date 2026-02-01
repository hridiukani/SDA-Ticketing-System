package com.sda.ticketing.web;

import com.sda.ticketing.domain.Attachment;
import com.sda.ticketing.domain.Ticket;
import com.sda.ticketing.domain.User;
import com.sda.ticketing.dto.AttachmentDto;
import com.sda.ticketing.repository.AttachmentRepository;
import com.sda.ticketing.repository.TicketRepository;
import com.sda.ticketing.repository.UserRepository;
import com.sda.ticketing.service.AuditLogService;
import com.sda.ticketing.service.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AttachmentController {

    private final TicketRepository ticketRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    public AttachmentController(TicketRepository ticketRepository,
                                AttachmentRepository attachmentRepository,
                                UserRepository userRepository,
                                FileStorageService fileStorageService,
                                AuditLogService auditLogService) {
        this.ticketRepository = ticketRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/api/tickets/{ticketId}/attachments")
    public ResponseEntity<AttachmentDto> uploadAttachment(@PathVariable Long ticketId,
                                                          @RequestParam("file") MultipartFile file) throws IOException {
        User current = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        String storagePath = fileStorageService.store(file);

        Attachment attachment = new Attachment();
        attachment.setTicket(ticket);
        attachment.setUploadedBy(current);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSizeBytes(file.getSize());
        attachment.setStoragePath(storagePath);
        Attachment saved = attachmentRepository.save(attachment);
        auditLogService.log(ticket, current, "ATTACHMENT_UPLOAD", "Attachment uploaded: " + saved.getFileName());

        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping("/api/tickets/{ticketId}/attachments")
    public ResponseEntity<List<AttachmentDto>> listAttachments(@PathVariable Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        List<AttachmentDto> result = attachmentRepository.findByTicketOrderByUploadedAtAsc(ticket).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/attachments/{id}/download")
    public void downloadAttachment(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
        Path path = fileStorageService.resolve(attachment.getStoragePath());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"");
        response.setContentType(attachment.getContentType() != null ? attachment.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Files.copy(path, response.getOutputStream());
        response.flushBuffer();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    private AttachmentDto toDto(Attachment attachment) {
        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setContentType(attachment.getContentType());
        dto.setSizeBytes(attachment.getSizeBytes());
        dto.setUploadedAt(attachment.getUploadedAt());
        dto.setUploadedById(attachment.getUploadedBy().getId());
        dto.setUploadedByName(attachment.getUploadedBy().getFullName());
        return dto;
    }
}

