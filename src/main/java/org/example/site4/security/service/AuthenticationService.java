package org.example.site4.security.service;

import org.example.site4.security.dto.JwtAuthenticationResponse;
import org.example.site4.security.dto.SignInRequest;
import org.example.site4.security.dto.SignUpRequest;

public interface AuthenticationService {
    JwtAuthenticationResponse signUp(SignUpRequest request);
    JwtAuthenticationResponse signIn(SignInRequest request);
}