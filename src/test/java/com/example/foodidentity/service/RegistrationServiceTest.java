package com.example.foodidentity.service;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.exception.UsernameAlreadyExistsException;
import com.example.foodidentity.model.RegistrationRequest;
import com.example.foodidentity.repository.UserFakeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserFakeRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;


    @Test
    void registerNewUserEncodesPasswordAndAssignsDefaultRole() {

        RegistrationRequest request = new RegistrationRequest("danny", "Hello@123");
        when(passwordEncoder.encode("Hello@123")).thenReturn("scrambled_hash");
        when(userRepository.registerIfAbsent(any(User.class))).thenReturn(true);

        registrationService.register(request);
        ArgumentCaptor<User> userCaptor = forClass(User.class);
        verify(userRepository).registerIfAbsent(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("scrambled_hash", capturedUser.getPassword(), "Password should be encoded");
        assertTrue(capturedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")),
                "User should have ROLE_USER");
    }

    @Test
    void registerDuplicateUserThrowsException() {
        RegistrationRequest request = new RegistrationRequest("clone", "password");
        when(passwordEncoder.encode("password")).thenReturn("hash");
        when(userRepository.registerIfAbsent(any(User.class))).thenReturn(false);

        assertThrows(UsernameAlreadyExistsException.class, () -> {
            registrationService.register(request);
        });
    }

}