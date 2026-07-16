package com.example.foodidentity.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.foodidentity.model.AuthCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(new AuthCredentials("admin", "user", "password", "a-secret-that-is-long-enough-for-tests"));

    @Test
    void generateTokenAssignsAdminRoleWhenAuthorityIsAdmin() {
        String token = jwtUtil.generateToken("alex", List.of(new SimpleGrantedAuthority("ADMIN")));

        assertEquals("alex", jwtUtil.extractUserName(token));
        assertEquals("ROLE_ADMIN", jwtUtil.getTokenAuthority(token).iterator().next().getAuthority());
    }

    @Test
    void generateTokenAssignsUserRoleWhenAuthorityIsNotAdmin() {
        String token = jwtUtil.generateToken("sam", List.of(new SimpleGrantedAuthority("USER")));

        assertEquals("ROLE_USER", jwtUtil.getTokenAuthority(token).iterator().next().getAuthority());
    }

    @Test
    void extractUserNameRejectsTokenSignedWithAnotherSecret() {
        JwtUtil anotherJwtUtil = new JwtUtil(new AuthCredentials("admin", "user", "password", "a-different-secret-that-is-long-enough"));
        String token = anotherJwtUtil.generateToken("alex", List.of(new SimpleGrantedAuthority("USER")));

        assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUserName(token));
    }
}
