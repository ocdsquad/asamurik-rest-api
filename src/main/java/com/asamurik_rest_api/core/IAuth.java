package com.asamurik_rest_api.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IAuth<T> {
    ResponseEntity<Object> register(T t, HttpServletRequest request);

    ResponseEntity<Object> verifyRegis(T t, HttpServletRequest request);

    ResponseEntity<Object> login(T t, HttpServletRequest request);

    ResponseEntity<Object> sendOTP(T t, HttpServletRequest request);

    ResponseEntity<Object> forgotPassword(T t, HttpServletRequest request);

    ResponseEntity<Object> resetPassword(T t, HttpServletRequest request);
}
