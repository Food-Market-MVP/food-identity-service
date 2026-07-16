package com.example.foodidentity.security;

import com.example.foodidentity.jwt.JwtAuthFilter;
import com.example.foodidentity.model.AuthCredentials;
import com.example.foodidentity.repository.UserFakeRepository;
import com.example.foodidentity.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void authenticationManagerAuthenticatesConfiguredUserWithMatchingPassword() throws Exception {
        SecurityConfig config = new SecurityConfig(mock(JwtAuthFilter.class));
        BCryptPasswordEncoder passwordEncoder = config.passwordEncoder();
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(
                new UserFakeRepository(new AuthCredentials("admin-security", "user-security", "$2a$10$D0cXnF.5YxgXGmBapVHcSeMmcRpxHWYmFm87xYhA./gmMaOneTuya", "secret")));
        AuthenticationManager authenticationManager = config.authenticationManager(userDetailsService, passwordEncoder);

        assertEquals("admin-security", authenticationManager.authenticate(
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated("admin-security", "123")).getName());
        assertTrue(passwordEncoder.matches("123", "$2a$10$D0cXnF.5YxgXGmBapVHcSeMmcRpxHWYmFm87xYhA./gmMaOneTuya"));
    }
}
