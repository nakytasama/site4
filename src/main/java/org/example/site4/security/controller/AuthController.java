package org.example.site4.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.site4.security.dto.JwtAuthenticationResponse;
import org.example.site4.security.dto.SignInRequest;
import org.example.site4.security.dto.SignUpRequest;
import org.example.site4.security.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request, HttpServletResponse response) {
        try {
            JwtAuthenticationResponse authResponse = authenticationService.signUp(request);
            setJwtCookie(response, authResponse.getToken());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            // Возвращаем JSON с ошибкой
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest request, HttpServletResponse response) {
        try {
            JwtAuthenticationResponse authResponse = authenticationService.signIn(request);
            setJwtCookie(response, authResponse.getToken());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            // Возвращаем JSON с ошибкой
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        // Удаляем cookie
        Cookie jwtCookie = new Cookie("jwtToken", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
    }
}