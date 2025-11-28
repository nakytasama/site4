package org.example.site4.controller;

import lombok.RequiredArgsConstructor;
import org.example.site4.security.domain.User;
import org.example.site4.security.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public User getProfile() {
        return userService.getCurrentUser();
    }

    // Обновление профиля пользователя
    @PutMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> updateProfile(@RequestBody User userDetails) {
        try {
            User currentUser = userService.getCurrentUser();

            // Обновляем только разрешённые поля (не юзернейм и не роль)
            if (userDetails.getEmail() != null) {
                currentUser.setEmail(userDetails.getEmail());
            }
            // Может быть будет имя и т.д

            User updatedUser = userService.save(currentUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Смена пароля
    @PutMapping("/password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestParam String newPassword) {
        // Написать потом логику смены пароля
        return ResponseEntity.ok().build();
    }
}