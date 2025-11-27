package org.example.site4.controller;

import lombok.RequiredArgsConstructor;
import org.example.site4.security.domain.User;
import org.example.site4.security.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public User getProfile() {
        return userService.getCurrentUser();
    }
}