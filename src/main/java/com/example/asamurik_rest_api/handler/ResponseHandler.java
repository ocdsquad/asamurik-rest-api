package com.example.asamurik_rest_api.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public ResponseEntity<Object> handleResponse(
            String message,
            HttpStatus status,
            Object data,
            Object errorCode,
            HttpServletRequest request
    ) {

        Map<String, Object> m = new HashMap<>();
        m.put("message", message);
        m.put("data", data == null ? "" : data);
        m.put("timestamp", LocalDateTime.now().toString());
        m.put("success", !status.isError());
        if (errorCode != null) {
            m.put("error-code", errorCode);
            m.put("path", request.getRequestURI());
        }
        return new ResponseEntity<>(m, status);
    }

    /*Contoh penggunaan success response
     * return responseHandler.handleResponse(SuccessCode.SUCCESS.getMessage(), HttpStatus.CREATED, data, null, request);
     * */

    public ResponseEntity<Object> handleResponse(
            String message,
            HttpStatus status,
            Object data,
            Object errorCode,
            WebRequest request
    ) {

        Map<String, Object> m = new HashMap<>();
        m.put("message", message);
//        m.put("status",status.value());
        m.put("data", data == null ? "" : data);
        m.put("timestamp", LocalDateTime.now().toString());
        m.put("success", !status.isError());
        if (errorCode != null) {
            m.put("error-code", errorCode);
            m.put("path", request.getContextPath());
        }
        return new ResponseEntity<>(m, status);
    }

    /*Contoh penggunaan error response
     * return responseHandler.handleResponse(status, HttpStatus.INTERNAL_SERVER_ERROR, null, ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), request);
     * */
}
