package com.example.foodidentity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1", headers = "Accept=application/json")
public class UserController {

    @GetMapping(value="")
    public String healthCheck() {
        return "Hello world";
    }
}
