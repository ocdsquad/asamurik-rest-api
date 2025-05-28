package com.asamurik_rest_api.dto.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VerifyOneTimePasswordDTO {
    @NotNull(message = "Email tidak boleh null")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    @Pattern(
            regexp = "^(?=.{1,64}@.{1,255}$)(?:(?![.])[a-zA-Z0-9._%+-]+(?:(?<!\\\\)[.][a-zA-Z0-9-]+)*?)@[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,50})+$",
            message = "Format email tidak valid, ex: reza_pangestu123@gmail.com"
    )
    private String email;

    @NotNull(message = "OTP tidak boleh null")
    @Pattern(regexp = "^[0-9]{6}$", message = "Format OTP tidak valid")
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
