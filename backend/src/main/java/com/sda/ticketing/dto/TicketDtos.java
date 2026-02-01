package com.sda.ticketing.dto;

import com.sda.ticketing.domain.Priority;
import com.sda.ticketing.domain.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class TicketCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Priority priority;

    private Long categoryId;
}

@Data
public class TicketUpdateStatusRequest {
    @NotNull
    private TicketStatus status;
}

@Data
public class TicketUpdatePriorityRequest {
    @NotNull
    private Priority priority;
}

@Data
public class AssignTicketRequest {
    @NotNull
    private Long assigneeId;
}

@Data
public class TicketSummaryDto {
    private Long id;
    private String title;
    private TicketStatus status;
    private Priority priority;
    private Long categoryId;
    private String categoryName;
    private Long requesterId;
    private String requesterName;
    private Long assigneeId;
    private String assigneeName;
    private Instant createdAt;
    private Instant updatedAt;
}

@Data
public class TicketDetailDto extends TicketSummaryDto {
    private String description;
}

