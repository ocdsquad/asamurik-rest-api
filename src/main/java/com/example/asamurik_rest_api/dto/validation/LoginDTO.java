package com.example.asamurik_rest_api.dto.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class LoginDTO {
    @NotNull(message = "Username Tidak Boleh null")
    @Pattern(
            regexp = "^([a-z0-9.]{3,50})$",
            message = "Format username tidak valid"
    )
    private String username;

    @NotNull(message = "Password Tidak Boleh null")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@_#\\-$]).{8,}$",
            message = "Format Password tidak valid"
    )
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
