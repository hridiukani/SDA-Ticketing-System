package com.sda.ticketing.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AttachmentDto {
    private Long id;
    private String fileName;
    private String contentType;
    private long sizeBytes;
    private Instant uploadedAt;
    private Long uploadedById;
    private String uploadedByName;
}

