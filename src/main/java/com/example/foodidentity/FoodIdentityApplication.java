package com.example.foodidentity;

import com.example.foodidentity.model.AuthCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AuthCredentials.class)
@SpringBootApplication
public class FoodIdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodIdentityApplication.class, args);
    }

}
