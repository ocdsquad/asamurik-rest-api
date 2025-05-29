package com.asamurik_rest_api.dto.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class OneTimePasswordDTO {
    @NotNull(message = "OTP tidak boleh null")
    @Pattern(regexp = "^[0-9]{6}$", message = "Format OTP tidak valid")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
