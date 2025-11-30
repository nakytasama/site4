package org.example.site4.controller;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Comment;
import org.example.site4.dto.CommentDTO;
import org.example.site4.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // Получить комментарии для изображения
    @GetMapping("/image/{imageId}")
    public List<CommentDTO> getCommentsByImage(@PathVariable Long imageId) {
        return commentService.getCommentsByImageIdAsDTO(imageId);
    }

    // Добавить комментарий
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Comment addComment(@RequestBody Comment comment) {
        return commentService.saveComment(comment);
    }

    // Удалить комментарий
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}