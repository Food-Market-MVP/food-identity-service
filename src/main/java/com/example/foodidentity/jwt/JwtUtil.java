package com.example.foodidentity.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.foodidentity.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

@Component
public class JwtUtil {
@Autowired
UserDetailsServiceImpl userDetailsService;
    private static final long EXPIRATION_TIME = 1000*60*60;//1HOUR
    private static final String SECRET = "my-super-secret-key-that-is-long-enough-1234567890!@#";// TODO: Will get updated
    public String generateToken(String userName, Collection<? extends GrantedAuthority> authorities) {

        boolean isAdmin = authorities.stream()
                .anyMatch(userRole -> userRole.getAuthority().equals("ADMIN"));

      String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";

        return JWT.create()
                .withSubject(userName)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("role", role)
                .sign(HMAC256(SECRET));
    }

    public String extractUserName(String token) {
        DecodedJWT decodedJWT = extractTokenInfo(token);
       return decodedJWT.getSubject();
    }

    private DecodedJWT extractTokenInfo(String token) {
        JWTVerifier jwtVerifier = JWT.require(HMAC256(SECRET)).build();
        return jwtVerifier.verify(token);
    }

    public Set<SimpleGrantedAuthority> getTokenAuthority(String token) {
        DecodedJWT decodedJWT = extractTokenInfo(token);
        return  Collections.singleton(new SimpleGrantedAuthority(decodedJWT.getClaim("role").toString()));
    }
}
