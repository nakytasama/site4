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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody SignUpRequest request, HttpServletResponse response) {
        JwtAuthenticationResponse authResponse = authenticationService.signUp(request);
        setJwtCookie(response, authResponse.getToken());
        return authResponse;
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest request, HttpServletResponse response) {
        JwtAuthenticationResponse authResponse = authenticationService.signIn(request);
        setJwtCookie(response, authResponse.getToken());
        return authResponse;
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setHttpOnly(true); // Защита от XSS
        jwtCookie.setSecure(false); // true для HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 часа
        response.addCookie(jwtCookie);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        // Удаляем cookie
        Cookie jwtCookie = new Cookie("jwtToken", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Удаляем cookie
        response.addCookie(jwtCookie);
    }
}