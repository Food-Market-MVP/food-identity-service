package com.example.foodidentity.controller;

import com.example.foodidentity.jwt.JwtUtil;
import com.example.foodidentity.model.AuthRequest;
import com.example.foodidentity.model.RegistrationRequest;
import com.example.foodidentity.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;

class ControllerTest {

    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final RegistrationService registrationService = mock(RegistrationService.class);
    private final AuthController authController = new AuthController(jwtUtil, authenticationManager, registrationService);

    @Test
    void generateTokenAuthenticatesRequestAndReturnsGeneratedToken() {
        Authentication authentication = mock(Authentication.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(eq("midlyn"), eq(authorities))).thenReturn("token");

        String token = authController.generateToken(new AuthRequest("midlyn", "password"));

        assertEquals("token", token);
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken("midlyn", authorities);
    }

    @Test
    void generateTokenPropagatesAuthenticationFailure() {
        IllegalArgumentException failure = new IllegalArgumentException("invalid credentials");
        when(authenticationManager.authenticate(any())).thenThrow(failure);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> authController.generateToken(new AuthRequest("midlyn", "wrong")));

        assertEquals(failure, thrown);
    }

    @Test
    void healthCheckReturnsExpectedGreeting() {
        assertEquals("Hello world", new UserController().healthCheck());
    }

    @Test
    void registerValidRequestReturnsCreatedStatus() {
        RegistrationRequest testForm = new RegistrationRequest("newuser", "Hello@123");

        ResponseEntity<Void> result = authController.register(testForm);
        assertEquals(
                CREATED,
                result.getStatusCode(),
                "Should return 201 Created"
        );

        verify(registrationService, times(1)).register(testForm);
    }
}
