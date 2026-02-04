package com.sda.ticketing.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTicketRequest {
    @NotNull
    private Long assigneeId;

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}

