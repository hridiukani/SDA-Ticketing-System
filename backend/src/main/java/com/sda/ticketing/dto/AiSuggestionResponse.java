package com.sda.ticketing.dto;

import com.sda.ticketing.domain.Priority;

public class AiSuggestionResponse {
    private Long suggestedCategoryId;
    private Priority suggestedPriority;
    private String rationale;

    public Long getSuggestedCategoryId() {
        return suggestedCategoryId;
    }

    public void setSuggestedCategoryId(Long suggestedCategoryId) {
        this.suggestedCategoryId = suggestedCategoryId;
    }

    public Priority getSuggestedPriority() {
        return suggestedPriority;
    }

    public void setSuggestedPriority(Priority suggestedPriority) {
        this.suggestedPriority = suggestedPriority;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}

