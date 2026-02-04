package com.sda.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateRequest {
    @NotBlank
    private String content;

    private boolean internalComment;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isInternalComment() {
        return internalComment;
    }

    public void setInternalComment(boolean internalComment) {
        this.internalComment = internalComment;
    }
}

