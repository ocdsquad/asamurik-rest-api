package com.asamurik_rest_api.common.response;

public enum ErrorCode {
    INVALID_EMAIL("invalid email address"),
    PASSWORD_TOO_SHORT("password must be at least 8 characters long"),
    USER_NOT_FOUND("iser not found"),
    INVALID_CREDENTIALS("invalid email or password"),
    UNAUTHORIZED("unauthorized access"),
    FORBIDDEN("forbidden access"),
    FILE_NOT_FOUND("file tidak ditemukan"),
    INTERNAL_SERVER_ERROR("internal server error");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
