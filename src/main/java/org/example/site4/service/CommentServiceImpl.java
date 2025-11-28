package org.example.site4.service;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Comment;
import org.example.site4.domain.Image;
import org.example.site4.security.domain.User;
import org.example.site4.repository.CommentRepository;
import org.example.site4.repository.ImageRepository;
import org.example.site4.security.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final UserService userService;

    @Override
    public List<Comment> getCommentsByImageId(Long imageId) {
        return commentRepository.findByImageIdOrderByCreatedAtDesc(imageId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public Comment saveComment(Comment comment) {
        // Проверка существования изображения
        Image image = imageRepository.findById(comment.getImage().getId())
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));

        // Текущий пользователь это автора комментария
        User currentUser = userService.getCurrentUser();
        comment.setUser(currentUser);
        comment.setImage(image);

        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, String newText) {
        return commentRepository.findById(id)
                .map(comment -> {
                    User currentUser = userService.getCurrentUser();
                    boolean isAuthor = comment.getUser().getId().equals(currentUser.getId());
                    boolean isAdmin = currentUser.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                    // Проверяем права - автор ИЛИ админ может редактировать
                    if (!isAuthor && !isAdmin) {
                        throw new RuntimeException("Вы можете редактировать только свои комментарии");
                    }

                    comment.setText(newText);
                    return commentRepository.save(comment);
                })
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        // Права (автор или админ может удалять)
        User currentUser = userService.getCurrentUser();
        boolean isAuthor = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("Вы можете удалять только свои комментарии");
        }

        commentRepository.delete(comment);
    }

    @Override
    public void deleteCommentsByImageId(Long imageId) {
        commentRepository.deleteByImageId(imageId);
    }

    @Override
    public void deleteCommentsByUserId(Long userId) {
        List<Comment> userComments = commentRepository.findByUserId(userId);
        commentRepository.deleteAll(userComments);
    }
}