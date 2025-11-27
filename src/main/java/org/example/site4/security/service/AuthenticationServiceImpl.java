package org.example.site4.security.service;

import lombok.RequiredArgsConstructor;
import org.example.site4.security.domain.Role;
import org.example.site4.security.domain.User;
import org.example.site4.security.dto.JwtAuthenticationResponse;
import org.example.site4.security.dto.SignInRequest;
import org.example.site4.security.dto.SignUpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        userService.create(user);
        return JwtAuthenticationResponse.builder().token(jwtService.generateToken(user)).build();
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        UserDetails user = userService.loadUserByUsername(request.getUsername());
        return JwtAuthenticationResponse.builder().token(jwtService.generateToken(user)).build();
    }
}