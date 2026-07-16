package com.example.foodidentity.controller;

import com.example.foodidentity.jwt.JwtUtil;
import com.example.foodidentity.model.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerTest {

    @Test
    void generateTokenAuthenticatesRequestAndReturnsGeneratedToken() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(eq("midlyn"), eq(authorities))).thenReturn("token");

        String token = new AuthController(jwtUtil, authenticationManager).generateToken(new AuthRequest("midlyn", "password"));

        assertEquals("token", token);
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken("midlyn", authorities);
    }

    @Test
    void generateTokenPropagatesAuthenticationFailure() {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        IllegalArgumentException failure = new IllegalArgumentException("invalid credentials");
        when(authenticationManager.authenticate(any())).thenThrow(failure);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> new AuthController(jwtUtil, authenticationManager).generateToken(new AuthRequest("midlyn", "wrong")));

        assertEquals(failure, thrown);
    }

    @Test
    void healthCheckReturnsExpectedGreeting() {
        assertEquals("Hello world", new UserController().healthCheck());
    }
}
