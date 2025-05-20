package com.example.asamurik_rest_api.handler;

import com.example.asamurik_rest_api.common.response.ErrorCode;
import com.example.asamurik_rest_api.common.response.GlobalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public static <T> ResponseEntity<GlobalResponse<T>> handleException(Exception ex) {
        GlobalResponse<T> response = new GlobalResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
