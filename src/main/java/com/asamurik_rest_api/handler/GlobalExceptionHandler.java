package com.asamurik_rest_api.handler;

import com.asamurik_rest_api.common.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    List<Map<String, Object>> errors = new ArrayList<>();

    @ExceptionHandler(FileNotFoundException.class)
    protected ResponseEntity<Object> handleFileNotFoundException(
            FileNotFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        logger.error("File not found: {}", ex.getMessage(), ex);
        return new ResponseHandler().handleResponse(
                ErrorCode.FILE_NOT_FOUND.getMessage(),
                HttpStatus.NOT_FOUND,
                null,
                null,
                request
        );
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        errors.clear();

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        for (FieldError error : fieldErrors) {
            Map<String, Object> mapError = new HashMap<>();
            mapError.put("field", error.getField());
            mapError.put("message", error.getDefaultMessage());
//            mapError.put("rejected-value", error.getRejectedValue());
            errors.add(mapError);
        }

        return new ResponseHandler().handleResponse(
                "Data tidak valid",
                HttpStatus.BAD_REQUEST,
                errors,
                null,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        ResponseEntity<Object> response = new ResponseHandler().handleResponse(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, (Object) null, (Object) null, (HttpServletRequest) null);
        return response;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        logger.error("Access denied: {}", ex.getMessage(), ex);

        return new ResponseHandler().handleResponse(
                ErrorCode.FORBIDDEN.getMessage(),
                HttpStatus.FORBIDDEN,
                null,
                null,
                request
        );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(
            Exception ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        logger.error("IO Exception: {}", ex.getMessage(), ex);
        return new ResponseHandler().handleResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                null,
                request
        );
    }
}
