package org.example.site4.security.service;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Image;
import org.example.site4.domain.Comment;
import org.example.site4.repository.ImageRepository;
import org.example.site4.repository.CommentRepository;
import org.example.site4.security.domain.User;
import org.example.site4.security.domain.Role;
import org.example.site4.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    public Optional<User> findByUsernameSafe(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        User currentUser = getCurrentUser();
        if (user.getId().equals(currentUser.getId())) {
            throw new RuntimeException("Ты не можешь поменять свою же роль");
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        User currentUser = getCurrentUser();
        if (user.getId().equals(currentUser.getId())) {
            throw new RuntimeException("Ты не можешь удалить себя");
        }

        try {
            // Удаляем все изображения пользователя
            List<Image> userImages = imageRepository.findByUserId(userId);
            for (Image image : userImages) {
                try {
                    Path filePath = Paths.get(uploadPath, image.getImagePath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("Не удалось удалить файл изображения: " + image.getImagePath());
                }

                // Удаляем комментарии
                commentRepository.deleteByImageId(image.getId());

                imageRepository.delete(image);
            }

            List<Comment> userComments = commentRepository.findByUserId(userId);
            commentRepository.deleteAll(userComments);

            // Удаляем пользователя
            userRepository.delete(user);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}