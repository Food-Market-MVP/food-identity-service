package com.example.foodidentity;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class FoodIdentityApplicationMainTest {

    @Test
    void mainStartsSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            FoodIdentityApplication.main(new String[]{"--spring.main.web-application-type=none"});

            springApplication.verify(() -> SpringApplication.run(FoodIdentityApplication.class,
                    new String[]{"--spring.main.web-application-type=none"}));
        }
    }
}
