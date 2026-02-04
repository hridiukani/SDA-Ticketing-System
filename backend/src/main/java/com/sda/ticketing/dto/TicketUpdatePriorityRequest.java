package com.sda.ticketing.dto;

import com.sda.ticketing.domain.Priority;
import jakarta.validation.constraints.NotNull;

public class TicketUpdatePriorityRequest {
    @NotNull
    private Priority priority;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}

