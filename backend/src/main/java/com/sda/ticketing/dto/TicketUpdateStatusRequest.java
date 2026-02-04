package com.sda.ticketing.dto;

import com.sda.ticketing.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class TicketUpdateStatusRequest {
    @NotNull
    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}

