package org.example.site4.repository;

import org.example.site4.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByImageIdOrderByCreatedAtDesc(Long imageId);
    List<Comment> findByUserId(Long userId);
    void deleteByImageId(Long imageId);
}