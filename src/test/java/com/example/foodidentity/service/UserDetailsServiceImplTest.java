package com.example.foodidentity.service;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.repository.UserFakeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private final UserFakeRepository repository = mock(UserFakeRepository.class);
    private final UserDetailsServiceImpl service = new UserDetailsServiceImpl(repository);

    @Test
    void loadUserByUsernameReturnsStoredUser() {
        User user = mock(User.class);
        when(repository.getUserByUsername("admin-test")).thenReturn(Optional.of(user));

        assertEquals("admin-test", service.loadUserByUsername("admin-test").getUsername());
    }

    @Test
    void loadUserByUsernameThrowsWhenUserIsMissing() {
        when(repository.getUserByUsername("missing")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
        assertEquals("User not found: missing", exception.getMessage());
    }

}
