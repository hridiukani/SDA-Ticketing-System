package com.sda.ticketing.service;

import com.sda.ticketing.domain.AuditLog;
import com.sda.ticketing.domain.Ticket;
import com.sda.ticketing.domain.User;
import com.sda.ticketing.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Ticket ticket, User actor, String actionType, String details) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setActor(actor);
        log.setActionType(actionType);
        log.setDetails(details);
        auditLogRepository.save(log);
    }
}

