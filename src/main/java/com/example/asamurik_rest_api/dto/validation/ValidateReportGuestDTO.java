package com.example.asamurik_rest_api.dto.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ValidateReportGuestDTO {
    @Size(min = 4, max = 100, message = "Nama lengkap harus antara 4-100 karakter")
    @Pattern(
            regexp = "^[a-zA-Z\\s]*$",
            message = "Nama lengkap hanya diperbolehkan alfabet dan spasi"
    )
    private String fullname;

    @NotNull(message = "Email tidak boleh null")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    @Pattern(
            regexp = "^(?=.{1,64}@.{1,255}$)(?:(?![.])[a-zA-Z0-9._%+-]+(?:(?<!\\\\)[.][a-zA-Z0-9-]+)*?)@[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,50})+$",
            message = "Format email tidak valid, ex: reza_pangestu123@gmail.com"
    )
    private String email;

    @NotBlank(message = "Pesan tidak boleh kosong")
    @Size(min = 10, max = 255, message = "Pesan harus antara 10-255 karakter")
    private String message;

    @NotNull(message = "OTP tidak boleh null")
    @Pattern(regexp = "^[0-9]{6}$",message = "Format OTP tidak valid")
    private String otp;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
