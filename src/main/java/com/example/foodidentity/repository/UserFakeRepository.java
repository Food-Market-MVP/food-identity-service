package com.example.foodidentity.repository;

import com.example.foodidentity.entity.User;
import com.example.foodidentity.model.AuthCredentials;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserFakeRepository {
    private static final List<User> USER_LIST = new ArrayList<>();
    public UserFakeRepository(AuthCredentials authCredentials) {
        User admin = new User(authCredentials.admin(), authCredentials.password(), Collections.singleton(new SimpleGrantedAuthority("ADMIN")) );

        User user = new User(authCredentials.user(), authCredentials.password(), Collections.singleton(new SimpleGrantedAuthority("USER")));
//password is 123
        USER_LIST.add(admin);
        USER_LIST.add(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return USER_LIST.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }
}
