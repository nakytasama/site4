package org.example.site4.controller;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Category;
import org.example.site4.domain.Comment;
import org.example.site4.domain.Image;
import org.example.site4.security.domain.User;
import org.example.site4.security.domain.Role;
import org.example.site4.security.service.UserService;
import org.example.site4.service.CategoryService;
import org.example.site4.service.CommentService;
import org.example.site4.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    // Работа с пользователем
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestParam Role role) {
        try {
            User updatedUser = userService.updateUserRole(userId, role);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Работа с изображениями
    @GetMapping("/images")
    public List<Image> getAllImages() {
        return imageService.getAllImages();
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }

    // Работа с категориями
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Работа с комментариями
    @GetMapping("/comments")
    public List<Comment> getAllComments() {
        // Позже добавлю метод commentservice для получения всех комментариев
        return List.of();
    }

    @GetMapping("/users/{userId}/comments")
    public List<Comment> getUserComments(@PathVariable Long userId) {
        return commentService.getCommentsByUserId(userId);
    }

    @GetMapping("/images/{imageId}/comments")
    public List<Comment> getImageComments(@PathVariable Long imageId) {
        return commentService.getCommentsByImageId(imageId);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestParam String text) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, text);
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/images/{imageId}/comments")
    public ResponseEntity<?> deleteImageComments(@PathVariable Long imageId) {
        try {
            commentService.deleteCommentsByImageId(imageId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}/comments")
    public ResponseEntity<?> deleteUserComments(@PathVariable Long userId) {
        try {
            commentService.deleteCommentsByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}