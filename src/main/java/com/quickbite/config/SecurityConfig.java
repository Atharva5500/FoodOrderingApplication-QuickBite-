package com.quickbite.config;

import com.quickbite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration          // Tells Spring this class defines beans
@EnableWebSecurity      // Enables Spring Security in the project
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    // ── Bean 1: PasswordEncoder ──────────────────────────────────
    // BCrypt is the industry standard for hashing passwords
    // It automatically adds a random salt — even identical passwords get different hashes
    // This is the bean AuthServiceImpl needs to encode passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Bean 2: UserDetailsService ───────────────────────────────
    // Tells Spring Security HOW to load a user from the database
    // Spring calls this automatically during login to find the user by email
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));
    }

    // ── Bean 3: AuthenticationProvider ──────────────────────────
    // Wires together UserDetailsService + PasswordEncoder
    // Spring uses this to validate login credentials:
    // 1. Load user via UserDetailsService
    // 2. Compare hashed password via PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── Bean 4: AuthenticationManager ────────────────────────────
    // This is the bean AuthServiceImpl uses in the login method
    // It delegates to AuthenticationProvider to validate credentials
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Bean 5: SecurityFilterChain ──────────────────────────────
    // Temporarily permits ALL requests so we can test the app
    // In Step 8 we will replace this with proper JWT-based security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ✅ Auth endpoints — public
                        .requestMatchers("/api/auth/**").permitAll()
                        // ✅ Swagger UI endpoints — must be public
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs"
                        ).permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // ✅ Basic Auth for testing — replaced with JWT in Step 8
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
