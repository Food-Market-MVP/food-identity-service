package com.example.foodidentity.repository;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.model.AuthCredentials;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserFakeRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserFakeRepository(AuthCredentials authCredentials) {
        User admin = new User(authCredentials.admin(), authCredentials.password(), Collections.singleton(new SimpleGrantedAuthority("ADMIN")) );

        User user = new User(authCredentials.user(), authCredentials.password(), Collections.singleton(new SimpleGrantedAuthority("USER")));
        users.put(admin.getUsername(), admin);
        users.put(user.getUsername(), user);
    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public boolean registerIfAbsent(User user) {
        return users.putIfAbsent(user.getUsername(), user) == null;
    }
}
