package com.sda.ticketing.dto;

import com.sda.ticketing.domain.Priority;
import lombok.Data;

@Data
public class AiSuggestionRequest {
    private String title;
    private String description;
}

@Data
public class AiSuggestionResponse {
    private Long suggestedCategoryId;
    private Priority suggestedPriority;
    private String rationale;
}

