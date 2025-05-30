package com.asamurik_rest_api.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ResetPasswordDTO {
    @NotNull(message = "Email tidak boleh null")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    @Pattern(
            regexp = "^(?=.{1,64}@.{1,255}$)(?:(?![.])[a-zA-Z0-9._%+-]+(?:(?<!\\\\)[.][a-zA-Z0-9-]+)*?)@[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,50})+$",
            message = "Format email tidak valid, ex: reza_pangestu123@gmail.com"
    )
    private String email;

    @NotNull(message = "Password baru tidak boleh null")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@_#\\-$]).*$",
            message = "Password baru minimal 1 angka, 1 huruf kecil, 1 huruf besar, dan 1 karakter seperti @_#-$"
    )
    @JsonProperty("new-password")
    private String newPassword;

    @NotBlank(message = "Konfirmasi password baru tidak boleh kosong")
    @JsonProperty("confirm-new-password")
    private String confirmNewPassword;

    @NotBlank(message = "Token tidak boleh kosong")
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    @AssertTrue(message = "Konfirmasi password baru tidak sama")
    public boolean isPasswordsMatch() {
        return this.newPassword.equals(this.confirmNewPassword);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
