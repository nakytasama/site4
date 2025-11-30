package org.example.site4.security.service;

import org.example.site4.security.domain.User;
import org.example.site4.security.domain.Role;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    // Существующие методы
    User getCurrentUser();
    User getByUsername(String username);
    User create(User user);
    User save(User user);

    // Новые методы для админки
    List<User> getAllUsers();
    Optional<User> getById(Long id);
    User updateUserRole(Long userId, Role role);
    void deleteUser(Long userId);

    Optional<User> findByUsernameSafe(String username);
}