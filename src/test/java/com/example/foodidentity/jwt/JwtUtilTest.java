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
import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(new AuthCredentials("admin", "user", "password", "a-secret-that-is-long-enough-for-tests"));

    @Test
    void generateTokenAssignsAdminRoleWhenAuthorityIsAdmin() {
        String token = jwtUtil.generateToken("midlyn", List.of(new SimpleGrantedAuthority("ADMIN")));

        assertEquals("midlyn", jwtUtil.extractUserName(token));
        assertTrue(jwtUtil.getTokenAuthority(token).stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void generateTokenAssignsUserRoleWhenAuthorityIsNotAdmin() {
        String token = jwtUtil.generateToken("montecito", List.of(new SimpleGrantedAuthority("USER")));

        assertTrue(jwtUtil.getTokenAuthority(token).stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void generateTokenAssignsAdminRoleWhenAdminIsNotFirstAuthority() {
        String token = jwtUtil.generateToken("midlyn", List.of(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN")));

        assertTrue(jwtUtil.getTokenAuthority(token).stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void extractUserNameRejectsTokenSignedWithDifferentSecret() {
        String token = JWT.create()
                .withSubject("midlyn")
                .sign(HMAC256("a-different-secret-that-is-long-enough"));

        assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUserName(token));
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

}
