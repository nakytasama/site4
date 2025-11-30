package org.example.site4.service;

import org.example.site4.domain.Comment;
import org.example.site4.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByImageId(Long imageId);
    List<Comment> getCommentsByUserId(Long userId);
    Comment saveComment(Comment comment);
    Comment updateComment(Long id, String newText);
    void deleteComment(Long id);
    void deleteCommentsByImageId(Long imageId);
    void deleteCommentsByUserId(Long userId);
    List<Comment> getAllComments();
    List<CommentDTO> getCommentsByImageIdAsDTO(Long imageId);
}