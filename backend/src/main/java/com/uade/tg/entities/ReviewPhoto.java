package com.uade.tg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "review_photos")
public class ReviewPhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_review", nullable = false)
    private Review review;
    
    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}

