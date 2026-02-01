package com.sda.ticketing.service;

import com.sda.ticketing.domain.*;
import com.sda.ticketing.dto.*;
import com.sda.ticketing.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final AttachmentRepository attachmentRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final TicketQueueNotifier ticketQueueNotifier;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository,
                         CommentRepository commentRepository,
                         AttachmentRepository attachmentRepository,
                         AuditLogService auditLogService,
                         NotificationService notificationService,
                         TicketQueueNotifier ticketQueueNotifier) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
        this.ticketQueueNotifier = ticketQueueNotifier;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    @Transactional
    public TicketDetailDto createTicket(TicketCreateRequest request) {
        User requester = getCurrentUser();

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setRequester(requester);
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            ticket.setCategory(category);
        }

        Ticket saved = ticketRepository.save(ticket);
        auditLogService.log(saved, requester, "CREATE", "Ticket created");
        notificationService.sendTicketCreated(saved, requester);
        ticketQueueNotifier.notifyQueueChanged();
        return toDetailDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<TicketSummaryDto> searchTickets(Optional<TicketStatus> status,
                                                Optional<Priority> priority,
                                                Optional<Long> categoryId,
                                                Optional<Long> assigneeId,
                                                Optional<Long> requesterId,
                                                Optional<String> search,
                                                Pageable pageable) {
        Specification<Ticket> spec = Specification.where(null);
        if (status.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status.get()));
        }
        if (priority.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority.get()));
        }
        if (categoryId.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId.get()));
        }
        if (assigneeId.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignee").get("id"), assigneeId.get()));
        }
        if (requesterId.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("requester").get("id"), requesterId.get()));
        }
        if (search.isPresent()) {
            String like = "%" + search.get().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            ));
        }
        return ticketRepository.findAll(spec, pageable).map(this::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public TicketDetailDto getTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailDto claimTicket(Long ticketId) {
        User current = getCurrentUser();
        if (current.getRole() == Role.REQUESTER) {
            throw new AccessDeniedException("Only technicians and admins can claim tickets");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setAssignee(current);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(Instant.now());
        auditLogService.log(ticket, current, "CLAIM", "Ticket claimed by " + current.getEmail());
        ticketQueueNotifier.notifyQueueChanged();
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailDto assignTicket(Long ticketId, AssignTicketRequest request) {
        User current = getCurrentUser();
        if (current.getRole() == Role.REQUESTER) {
            throw new AccessDeniedException("Only technicians and admins can assign tickets");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        ticket.setAssignee(assignee);
        ticket.setUpdatedAt(Instant.now());
        auditLogService.log(ticket, current, "ASSIGN", "Assigned to " + assignee.getEmail());
        ticketQueueNotifier.notifyQueueChanged();
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailDto changeStatus(Long ticketId, TicketUpdateStatusRequest request) {
        User current = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!canModifyTicket(current, ticket)) {
            throw new AccessDeniedException("Not allowed to change status");
        }
        ticket.setStatus(request.getStatus());
        if (request.getStatus() == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(Instant.now());
        }
        ticket.setUpdatedAt(Instant.now());
        auditLogService.log(ticket, current, "STATUS_CHANGE", "Status changed to " + request.getStatus());
        ticketQueueNotifier.notifyQueueChanged();
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailDto changePriority(Long ticketId, TicketUpdatePriorityRequest request) {
        User current = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!canModifyTicket(current, ticket)) {
            throw new AccessDeniedException("Not allowed to change priority");
        }
        ticket.setPriority(request.getPriority());
        ticket.setUpdatedAt(Instant.now());
        auditLogService.log(ticket, current, "PRIORITY_CHANGE", "Priority changed to " + request.getPriority());
        ticketQueueNotifier.notifyQueueChanged();
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailDto closeTicket(Long ticketId) {
        TicketUpdateStatusRequest request = new TicketUpdateStatusRequest();
        request.setStatus(TicketStatus.CLOSED);
        return changeStatus(ticketId, request);
    }

    @Transactional
    public TicketDetailDto reopenTicket(Long ticketId) {
        TicketUpdateStatusRequest request = new TicketUpdateStatusRequest();
        request.setStatus(TicketStatus.REOPENED);
        return changeStatus(ticketId, request);
    }

    private boolean canModifyTicket(User user, Ticket ticket) {
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.TECHNICIAN) {
            return true;
        }
        return user.getRole() == Role.REQUESTER && ticket.getRequester().getId().equals(user.getId());
    }

    private TicketSummaryDto toSummaryDto(Ticket ticket) {
        TicketSummaryDto dto = new TicketSummaryDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        if (ticket.getCategory() != null) {
            dto.setCategoryId(ticket.getCategory().getId());
            dto.setCategoryName(ticket.getCategory().getName());
        }
        if (ticket.getRequester() != null) {
            dto.setRequesterId(ticket.getRequester().getId());
            dto.setRequesterName(ticket.getRequester().getFullName());
        }
        if (ticket.getAssignee() != null) {
            dto.setAssigneeId(ticket.getAssignee().getId());
            dto.setAssigneeName(ticket.getAssignee().getFullName());
        }
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        return dto;
    }

    private TicketDetailDto toDetailDto(Ticket ticket) {
        TicketDetailDto dto = new TicketDetailDto();
        TicketSummaryDto summary = toSummaryDto(ticket);
        dto.setId(summary.getId());
        dto.setTitle(summary.getTitle());
        dto.setStatus(summary.getStatus());
        dto.setPriority(summary.getPriority());
        dto.setCategoryId(summary.getCategoryId());
        dto.setCategoryName(summary.getCategoryName());
        dto.setRequesterId(summary.getRequesterId());
        dto.setRequesterName(summary.getRequesterName());
        dto.setAssigneeId(summary.getAssigneeId());
        dto.setAssigneeName(summary.getAssigneeName());
        dto.setCreatedAt(summary.getCreatedAt());
        dto.setUpdatedAt(summary.getUpdatedAt());
        dto.setDescription(ticket.getDescription());
        return dto;
    }
}

