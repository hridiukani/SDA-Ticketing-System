package com.sda.ticketing.web;

import com.sda.ticketing.domain.Comment;
import com.sda.ticketing.domain.Role;
import com.sda.ticketing.domain.Ticket;
import com.sda.ticketing.domain.User;
import com.sda.ticketing.dto.CommentCreateRequest;
import com.sda.ticketing.dto.CommentDto;
import com.sda.ticketing.repository.CommentRepository;
import com.sda.ticketing.repository.TicketRepository;
import com.sda.ticketing.repository.UserRepository;
import com.sda.ticketing.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public CommentController(TicketRepository ticketRepository,
                             CommentRepository commentRepository,
                             UserRepository userRepository,
                             AuditLogService auditLogService) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@PathVariable Long ticketId,
                                                 @Valid @RequestBody CommentCreateRequest request) {
        User current = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (request.isInternalComment()
                && current.getRole() == Role.REQUESTER) {
            throw new AccessDeniedException("Requesters cannot create internal comments");
        }

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(current);
        comment.setContent(request.getContent());
        comment.setInternalComment(request.isInternalComment());
        Comment saved = commentRepository.save(comment);
        auditLogService.log(ticket, current, "COMMENT", "Comment added");
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> listComments(@PathVariable Long ticketId) {
        User current = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        boolean canSeeInternal = current.getRole() == Role.ADMIN || current.getRole() == Role.TECHNICIAN;

        List<CommentDto> result = commentRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
            .filter(c -> !c.isInternalComment() || canSeeInternal)
            .map(this::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorName(comment.getAuthor().getFullName());
        dto.setContent(comment.getContent());
        dto.setInternalComment(comment.isInternalComment());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}

