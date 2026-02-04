package com.sda.ticketing.dto;

public class TicketDetailDto extends TicketSummaryDto {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

