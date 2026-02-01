package com.sda.ticketing.repository;

import com.sda.ticketing.domain.Attachment;
import com.sda.ticketing.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByTicketOrderByUploadedAtAsc(Ticket ticket);
}

