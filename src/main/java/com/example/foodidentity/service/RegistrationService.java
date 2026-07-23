package com.example.foodidentity.service;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.exception.UsernameAlreadyExistsException;
import com.example.foodidentity.model.RegistrationRequest;
import com.example.foodidentity.repository.UserFakeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RegistrationService {
    private final UserFakeRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserFakeRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationRequest registrationRequest) {
        User user = new User(
                registrationRequest.username(),
                passwordEncoder.encode(registrationRequest.password()),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        if (!userRepository.registerIfAbsent(user)) {
            throw new UsernameAlreadyExistsException(registrationRequest.username());
        }
    }
}
