package com.sda.ticketing.web;

import com.sda.ticketing.domain.Priority;
import com.sda.ticketing.domain.TicketStatus;
import com.sda.ticketing.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final TicketRepository ticketRepository;

    public ReportController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/tickets-by-status")
    public ResponseEntity<Map<TicketStatus, Long>> ticketsByStatus() {
        Map<TicketStatus, Long> result = new EnumMap<>(TicketStatus.class);
        for (TicketStatus status : TicketStatus.values()) {
            long count = ticketRepository.count((root, query, cb) -> cb.equal(root.get("status"), status));
            result.put(status, count);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tickets-by-priority")
    public ResponseEntity<Map<Priority, Long>> ticketsByPriority() {
        Map<Priority, Long> result = new EnumMap<>(Priority.class);
        for (Priority priority : Priority.values()) {
            long count = ticketRepository.count((root, query, cb) -> cb.equal(root.get("priority"), priority));
            result.put(priority, count);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary() {
        Map<String, Object> result = new HashMap<>();
        long total = ticketRepository.count();
        long open = ticketRepository.count((root, query, cb) -> cb.equal(root.get("status"), TicketStatus.OPEN));
        long resolved = ticketRepository.count((root, query, cb) -> cb.equal(root.get("status"), TicketStatus.RESOLVED));
        result.put("totalTickets", total);
        result.put("openTickets", open);
        result.put("resolvedTickets", resolved);
        return ResponseEntity.ok(result);
    }
}

