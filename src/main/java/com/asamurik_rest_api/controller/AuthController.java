package com.asamurik_rest_api.controller;

import com.asamurik_rest_api.dto.validation.EmailDTO;
import com.asamurik_rest_api.dto.validation.LoginDTO;
import com.asamurik_rest_api.dto.validation.RegistrationDTO;
import com.asamurik_rest_api.dto.validation.VerifyOneTimePasswordDTO;
import com.asamurik_rest_api.service.AuthService;
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

    @PostMapping("/verify-regis")
    public ResponseEntity<Object> verifyRegis(
            @Valid @RequestBody VerifyOneTimePasswordDTO verifyOneTimePasswordDTO,
            HttpServletRequest request
    ) {
        return authService.verifyRegis(authService.mapToUser(verifyOneTimePasswordDTO), request);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletRequest request
    ) {
        return authService.login(authService.mapToUser(loginDTO), request);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Object> sendOtp(
            @Valid @RequestBody EmailDTO emailDTO,
            HttpServletRequest request
    ) {
        return authService.sendOTP(authService.mapToUser(emailDTO), request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(
            @Valid @RequestBody EmailDTO emailDTO,
            HttpServletRequest request
    ) {
        return authService.forgotPassword(emailDTO.getEmail(), request);
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<Object> verifyForgotPassword(
            @Valid @RequestBody VerifyOneTimePasswordDTO verifyOneTimePasswordDTO,
            HttpServletRequest request
    ) {
        return authService.verifyForgotPassword(authService.mapToUser(verifyOneTimePasswordDTO), request);
    }
}
