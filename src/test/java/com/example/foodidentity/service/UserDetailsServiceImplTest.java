package com.example.foodidentity.service;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.model.AuthCredentials;
import com.example.foodidentity.repository.UserFakeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDetailsServiceImplTest {

    private final AuthCredentials credentials = new AuthCredentials("admin-test", "user-test", "password", "secret");

    @Test
    void getUserByUsernameFindsConfiguredUsersAndReturnsEmptyForUnknownUser() {
        UserFakeRepository repository = new UserFakeRepository(credentials);

        assertEquals("admin-test", repository.getUserByUsername("admin-test").orElseThrow().getUsername());
        assertEquals("user-test", repository.getUserByUsername("user-test").orElseThrow().getUsername());
        assertEquals(true, repository.getUserByUsername("unknown").isEmpty());
    }

    @Test
    void loadUserByUsernameReturnsStoredUserAndRejectsUnknownUser() {
        UserFakeRepository repository = new UserFakeRepository(credentials);
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(repository);

        assertEquals("admin-test", service.loadUserByUsername("admin-test").getUsername());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
        assertEquals("User not found: missing", exception.getMessage());
    }

    @Test
    void userExposesAndUpdatesAccountStateFields() {
        User user = new User();
        user.setUsername("midlyn");
        user.setPassword("secret");
        user.setAuthorities(Set.of(() -> "ROLE_USER"));

        assertEquals("midlyn", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
        assertEquals(true, user.isAccountNonExpired());
        assertEquals(true, user.isAccountNonLocked());
        assertEquals(true, user.isCredentialsNonExpired());
        assertEquals(true, user.isEnabled());
    }
}
