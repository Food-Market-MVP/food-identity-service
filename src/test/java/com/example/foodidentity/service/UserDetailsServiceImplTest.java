package com.example.foodidentity.service;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.repository.UserFakeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserFakeRepository userDao;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_UserExists_ReturnUserDetails() {
        Set<GrantedAuthority> authority = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        User expectedUser = new User("danny", "password123", authority);

        when(userDao.getUserByUsername(expectedUser.getUsername())).thenReturn(Optional.of(expectedUser));

        UserDetails result = userDetailsService.loadUserByUsername(expectedUser.getUsername());

        assertNotNull(result, "The returned user should not be null");
        assertEquals(expectedUser.getUsername(), result.getUsername(), "The username should match the requested user");
        assertEquals(expectedUser.getPassword(), result.getPassword(), "The password should match the requested password" );

        verify(userDao, times(1)).getUserByUsername(expectedUser.getUsername());
    }

    @Test
    void loadUserByUsername_UserDoesNotExists_ThrowsException() {
        String unknownName = "ghost";

        when(userDao.getUserByUsername(unknownName)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {userDetailsService.loadUserByUsername(unknownName);});

        verify(userDao, times(1)).getUserByUsername(unknownName);
    }
}
