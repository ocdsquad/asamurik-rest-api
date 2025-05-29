package com.asamurik_rest_api.entity;

public enum ItemStatus {
    FRESH("Fresh"),
    ON_PROGRESS("On Progress"),
    FOUND("Found");

    private final String status;

    ItemStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
