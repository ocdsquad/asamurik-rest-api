package com.example.asamurik_rest_api.controller;

import com.example.asamurik_rest_api.dto.validation.RegistrationDTO;
import com.example.asamurik_rest_api.entity.User;
import com.example.asamurik_rest_api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegistrationDTO registrationDTO,
            HttpServletRequest request
    ) {
        return authService.register(authService.mapToUser(registrationDTO), request);
    }
}
