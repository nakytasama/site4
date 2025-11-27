package org.example.site4.security.service;

import org.example.site4.security.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getCurrentUser();
    User getByUsername(String username);
    User create(User user);
    User save(User user);
}