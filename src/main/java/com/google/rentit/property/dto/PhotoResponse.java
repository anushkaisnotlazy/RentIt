package com.google.rentit.property.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PhotoResponse {
    private Long id;
    private String imageUrl;
    private String thumbnailUrl;
    private String caption;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private LocalDateTime uploadedAt;

    
}
