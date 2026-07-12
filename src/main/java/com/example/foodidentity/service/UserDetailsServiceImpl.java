package com.example.foodidentity.service;

import com.example.foodidentity.repository.UserFakeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserFakeRepository userDao;

    public UserDetailsServiceImpl(UserFakeRepository userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userDao.getUserByUsername(username).isEmpty()) {
            throw new RuntimeException("no user found");
        }

        return userDao.getUserByUsername(username).get();
    }
}
