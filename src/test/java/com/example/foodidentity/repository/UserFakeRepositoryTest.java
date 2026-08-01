package com.example.foodidentity.repository;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.model.AuthCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserFakeRepositoryTest {

    private UserFakeRepository repository;

    @BeforeEach
    void setUp() {
        AuthCredentials credentials = new AuthCredentials("admin", "user", "password", "secret");
        repository = new UserFakeRepository(credentials);
    }

    @Test
    void registerIfAbsentNewUserReturnsTrue() {
        User newUser = new User("brandnewuser", "hash", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        boolean result = repository.registerIfAbsent(newUser);
        assertTrue(result, "Should return true when registering a new user");

        assertTrue(repository.getUserByUsername("brandnewuser").isPresent());
    }

    @Test
    void registerIfAbsentDuplicateUserReturnsFalse() {
        User duplicateUser = new User("user", "hash", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        boolean result = repository.registerIfAbsent(duplicateUser);
        assertFalse(result, "Should return false because 'user' already exists");
    }
}