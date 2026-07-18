package com.example.foodidentity.security;

import com.example.foodidentity.jwt.JwtAuthFilter;
import com.example.foodidentity.model.AuthCredentials;
import com.example.foodidentity.repository.UserFakeRepository;
import com.example.foodidentity.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    private final JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);
    private final SecurityConfig config = new SecurityConfig(jwtAuthFilter);

    @Test
    void authenticationManagerUsesConfiguredPasswordEncoderToMatchPassword() throws Exception {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(
                new UserFakeRepository(new AuthCredentials("mock-admin-security", "mock-user-security", "encoded-password", "secret")));
        AuthenticationManager authenticationManager = config.authenticationManager(userDetailsService, passwordEncoder);

        when(passwordEncoder.matches("123", "encoded-password")).thenReturn(true);

        assertEquals("mock-admin-security", authenticationManager.authenticate(
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated("mock-admin-security", "123")).getName());

        verify(passwordEncoder).matches("123", "encoded-password");
    }
}
