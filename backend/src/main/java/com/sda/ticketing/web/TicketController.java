package com.sda.ticketing.web;

import com.sda.ticketing.domain.Priority;
import com.sda.ticketing.domain.TicketStatus;
import com.sda.ticketing.dto.*;
import com.sda.ticketing.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketDetailDto> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @GetMapping
    public ResponseEntity<Page<TicketSummaryDto>> searchTickets(
            @RequestParam Optional<TicketStatus> status,
            @RequestParam Optional<Priority> priority,
            @RequestParam Optional<Long> categoryId,
            @RequestParam Optional<Long> assigneeId,
            @RequestParam Optional<Long> requesterId,
            @RequestParam Optional<String> search,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ticketService.searchTickets(
                status, priority, categoryId, assigneeId, requesterId, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDto> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<TicketDetailDto> claimTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.claimTicket(id));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TicketDetailDto> assignTicket(@PathVariable Long id,
                                                        @Valid @RequestBody AssignTicketRequest request) {
        return ResponseEntity.ok(ticketService.assignTicket(id, request));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<TicketDetailDto> changeStatus(@PathVariable Long id,
                                                        @Valid @RequestBody TicketUpdateStatusRequest request) {
        return ResponseEntity.ok(ticketService.changeStatus(id, request));
    }

    @PostMapping("/{id}/priority")
    public ResponseEntity<TicketDetailDto> changePriority(@PathVariable Long id,
                                                          @Valid @RequestBody TicketUpdatePriorityRequest request) {
        return ResponseEntity.ok(ticketService.changePriority(id, request));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<TicketDetailDto> closeTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.closeTicket(id));
    }

    @PostMapping("/{id}/reopen")
    public ResponseEntity<TicketDetailDto> reopenTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.reopenTicket(id));
    }
}

