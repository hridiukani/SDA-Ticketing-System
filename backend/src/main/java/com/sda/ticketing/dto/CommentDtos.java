package com.sda.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class CommentCreateRequest {
    @NotBlank
    private String content;

    private boolean internalComment;
}

@Data
public class CommentDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private String content;
    private boolean internalComment;
    private Instant createdAt;
}

