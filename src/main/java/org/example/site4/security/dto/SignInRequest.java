package org.example.site4.security.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}