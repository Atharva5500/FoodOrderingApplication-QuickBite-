package com.quickbite.security;

import org.springframework.stereotype.Component;

// Stub class — we will implement this fully in Step 8
// Created now just to remove IDE errors in AuthServiceImpl
@Component
public class JwtUtil {

    public String generateToken(String email, String role) {
        // Full implementation coming in Step 8
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
