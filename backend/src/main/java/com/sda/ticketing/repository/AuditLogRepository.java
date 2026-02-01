package com.sda.ticketing.repository;

import com.sda.ticketing.domain.AuditLog;
import com.sda.ticketing.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}

