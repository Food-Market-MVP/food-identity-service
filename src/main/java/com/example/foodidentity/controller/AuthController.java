package com.example.foodidentity.controller;


import com.example.foodidentity.jwt.JwtUtil;
import com.example.foodidentity.model.AuthRequest;
import com.example.foodidentity.model.RegistrationRequest;
import com.example.foodidentity.service.RegistrationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1", headers = "Accept=application/json")
public class AuthController {
private final JwtUtil jwtUtil;
private final AuthenticationManager authenticationManager;
private final RegistrationService registrationService;

    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                          RegistrationService registrationService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.registrationService = registrationService;
    }


    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) {
        try {

         Authentication authentication =  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
            return jwtUtil.generateToken(authRequest.username(), authentication.getAuthorities());
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationRequest registrationRequest) {
        registrationService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
