package com.asamurik_rest_api.common.response;

public enum SuccessCode {
    GET_USER_SUCCESS("User retrieved successfully"),
    GET_ITEM_SUCCESS("Item retrieved successfully"),
    GET_CATEGORY_SUCCESS("Category retrieved successfully"),
    ADD_USER_SUCCESS("User added successfully"),
    ADD_ITEM_SUCCESS("Item added successfully"),
    ADD_CATEGORY_SUCCESS("Category added successfully"),
    UPDATE_USER_SUCCESS("User updated successfully"),
    UPDATE_ITEM_SUCCESS("Item updated successfully"),
    DELETE_USER_SUCCESS("User deleted successfully"),
    DELETE_ITEM_SUCCESS("Item deleted successfully"),
    DELETE_CATEGORY_SUCCESS("Category deleted successfully");

    private final String message;

    SuccessCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
