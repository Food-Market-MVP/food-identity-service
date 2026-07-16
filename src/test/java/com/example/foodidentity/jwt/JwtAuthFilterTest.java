package com.example.foodidentity.jwt;

import com.auth0.jwt.JWT;
import com.example.foodidentity.model.AuthCredentials;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JwtAuthFilterTest {

    private final AuthCredentials credentials = new AuthCredentials("admin", "user", "password", "a-secret-that-is-long-enough-for-tests");
    private final JwtUtil jwtUtil = new JwtUtil(credentials);
    private final JwtAuthFilter filter = new JwtAuthFilter(jwtUtil);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalContinuesWhenAuthorizationHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalContinuesWhenAuthorizationHeaderIsNotBearerToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic credentials");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalRejectsInvalidBearerToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals(403, response.getStatus());
    }

    @Test
    void doFilterInternalAuthenticatesValidTokenWhenContextIsEmpty() throws Exception {
        String token = jwtUtil.generateToken("sam", List.of(() -> "USER"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals("sam", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals("ROLE_USER", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternalKeepsExistingAuthenticationAndContinuesForTokenWithoutSubject() throws Exception {
        UsernamePasswordAuthenticationToken existingAuthentication = UsernamePasswordAuthenticationToken.authenticated("existing", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);
        String token = JWT.create().withClaim("role", "ROLE_USER").sign(HMAC256(credentials.secret()));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternalKeepsExistingAuthenticationForValidTokenWithSubject() throws Exception {
        UsernamePasswordAuthenticationToken existingAuthentication = UsernamePasswordAuthenticationToken.authenticated("existing", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);
        String token = jwtUtil.generateToken("sam", List.of(() -> "USER"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
