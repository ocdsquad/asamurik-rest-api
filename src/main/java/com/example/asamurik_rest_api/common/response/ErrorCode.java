package com.example.asamurik_rest_api.common.response;

public enum ErrorCode {
    INVALID_EMAIL("Invalid email address"),
    PASSWORD_TOO_SHORT("Password must be at least 8 characters long"),
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid email or password"),
    UNAUTHORIZED("Unauthorized access"),
    FORBIDDEN("Forbidden access"),
    INTERNAL_SERVER_ERROR("Internal server error");


    private final String message;
    ErrorCode(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
