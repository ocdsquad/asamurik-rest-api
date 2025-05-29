package com.asamurik_rest_api.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ResetPasswordDTO {
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
}
