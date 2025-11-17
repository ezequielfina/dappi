package com.uade.tg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPhotoDTO {
    private Long id;
    private String filename;
    private String filePath;
    private String contentType;
    private Long fileSize;
}

