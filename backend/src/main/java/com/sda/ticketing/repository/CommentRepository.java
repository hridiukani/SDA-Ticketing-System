package com.sda.ticketing.repository;

import com.sda.ticketing.domain.Comment;
import com.sda.ticketing.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}

