package com.quickbite.security;

import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    public String generateToken(String email, String role) {

        return null;
    }

    public String extractEmail(String token) {
        return null;
    }

    public String extractRole(String token) {
        return null;
    }

    public boolean validateToken(String token) {
        return false;
    }
}
