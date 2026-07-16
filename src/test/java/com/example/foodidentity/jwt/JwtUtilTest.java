package com.example.foodidentity.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.foodidentity.model.AuthCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(new AuthCredentials("admin", "user", "password", "a-secret-that-is-long-enough-for-tests"));

    @Test
    void generateTokenAssignsAdminRoleWhenAuthorityIsAdmin() {
        String token = jwtUtil.generateToken("midlyn", List.of(new SimpleGrantedAuthority("ADMIN")));

        assertEquals("midlyn", jwtUtil.extractUserName(token));
        assertEquals("ROLE_ADMIN", jwtUtil.getTokenAuthority(token).iterator().next().getAuthority());
    }

    @Test
    void generateTokenAssignsUserRoleWhenAuthorityIsNotAdmin() {
        String token = jwtUtil.generateToken("montecito", List.of(new SimpleGrantedAuthority("USER")));

        assertEquals("ROLE_USER", jwtUtil.getTokenAuthority(token).iterator().next().getAuthority());
    }

    @Test
    void generateTokenAssignsAdminRoleWhenAdminIsNotFirstAuthority() {
        String token = jwtUtil.generateToken("midlyn", List.of(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN")));

        assertEquals("ROLE_ADMIN", jwtUtil.getTokenAuthority(token).iterator().next().getAuthority());
    }

    @Test
    void generatedTokenExpiresApproximatelyFiveMinutesAfterCreation() {
        long beforeGeneration = System.currentTimeMillis();

        String token = jwtUtil.generateToken("midlyn", List.of(new SimpleGrantedAuthority("USER")));

        long afterGeneration = System.currentTimeMillis();
        DecodedJWT decodedJWT = JWT.decode(token);
        Date issuedAt = decodedJWT.getIssuedAt();
        Date expiresAt = decodedJWT.getExpiresAt();

        long tokenLifetime = expiresAt.getTime() - issuedAt.getTime();
        assertEquals(300_000L, tokenLifetime);

        assertTrue(issuedAt.getTime() >= beforeGeneration - 1_000 && issuedAt.getTime() <= afterGeneration + 1_000);


    }

    @Test
    void extractUserNameRejectsTokenSignedWithAnotherSecret() {
        JwtUtil anotherJwtUtil = new JwtUtil(new AuthCredentials("admin", "user", "password", "a-different-secret-that-is-long-enough"));
        String token = anotherJwtUtil.generateToken("midlyn", List.of(new SimpleGrantedAuthority("USER")));

        assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUserName(token));
    }
}
