package com.quickbite.config;

import com.quickbite.entity.User;
import com.quickbite.enums.Role;
import com.quickbite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// CommandLineRunner runs automatically after Spring Boot starts
// Perfect for seeding initial data
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Only seed if admin doesn't exist yet
        // This check prevents duplicate admin on every restart
        if (!userRepository.existsByEmail("admin@quickbite.com")) {

            User admin = User.builder()
                    .name("Admin")
                    .email("admin@quickbite.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("9999999999")
                    .address("QuickBite HQ, Bengaluru")
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            System.out.println("✅ Admin account created: admin@quickbite.com / admin123");
        } else {
            System.out.println("✅ Admin account already exists");
        }
    }
}


