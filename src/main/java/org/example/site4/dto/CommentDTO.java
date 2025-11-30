package org.example.site4.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private String username;
    private Long userId;
    private Long imageId;

    public CommentDTO(Long id, String text, LocalDateTime createdAt, String username, Long userId, Long imageId) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.username = username;
        this.userId = userId;
        this.imageId = imageId;
    }
}