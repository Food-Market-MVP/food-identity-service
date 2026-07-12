package com.example.foodidentity.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix ="app.security" )
public record AuthCredentials (String admin, String user, String password) { }
