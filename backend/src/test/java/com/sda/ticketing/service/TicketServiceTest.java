package com.sda.ticketing.service;

import com.sda.ticketing.domain.Priority;
import com.sda.ticketing.domain.Role;
import com.sda.ticketing.domain.Ticket;
import com.sda.ticketing.domain.User;
import com.sda.ticketing.dto.TicketCreateRequest;
import com.sda.ticketing.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TicketServiceTest {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private CommentRepository commentRepository;
    private AttachmentRepository attachmentRepository;
    private AuditLogService auditLogService;
    private NotificationService notificationService;
    private TicketQueueNotifier ticketQueueNotifier;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        commentRepository = mock(CommentRepository.class);
        attachmentRepository = mock(AttachmentRepository.class);
        auditLogService = mock(AuditLogService.class);
        notificationService = mock(NotificationService.class);
        ticketQueueNotifier = mock(TicketQueueNotifier.class);

        ticketService = new TicketService(ticketRepository, userRepository, categoryRepository,
                commentRepository, attachmentRepository, auditLogService, notificationService, ticketQueueNotifier);
    }

    @Test
    void createTicketUsesCurrentUserAsRequester() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPasswordHash("x");
        user.setFullName("User");
        user.setRole(Role.REQUESTER);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(user.getEmail(), "pw"));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(123L);
            return t;
        });

        TicketCreateRequest req = new TicketCreateRequest();
        req.setTitle("Test ticket");
        req.setDescription("Details");
        req.setPriority(Priority.MEDIUM);

        var dto = ticketService.createTicket(req);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        Ticket saved = captor.getValue();

        assertThat(saved.getRequester()).isEqualTo(user);
        assertThat(dto.getId()).isEqualTo(123L);
        verify(auditLogService).log(any(), eq(user), eq("CREATE"), anyString());
    }
}

