package com.asamurik_rest_api.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {
    @NotNull(message = "Nama lengkap tidak boleh null")
    @Size(min = 4, max = 100, message = "Nama lengkap harus antara 4-100 karakter")
    @Pattern(
            regexp = "^[a-zA-Z\\s]*$",
            message = "Nama lengkap hanya boleh huruf dan spasi, contoh: Reza Pangestu"
    )
    private String fullname;

    @NotNull(message = "Username tidak boleh null")
    @Size(min = 3, max = 50, message = "Username harus antara 3-50 karakter")
    @Pattern(
            regexp = "^[a-z0-9.]*$",
            message = "Username hanya boleh huruf kecil, angka dan titik, contoh: reza.pangestu123"
    )
    private String username;

    @NotNull(message = "Email tidak boleh null")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    @Pattern(
            regexp = "^(?=.{1,64}@.{1,255}$)(?:(?![.])[a-zA-Z0-9._%+-]+(?:(?<!\\\\)[.][a-zA-Z0-9-]+)*?)@[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,50})+$",
            message = "Format email tidak valid, contoh: reza_pangestu123@gmail.com"
    )
    private String email;

    @NotNull(message = "Password tidak boleh null")
    @Size(min = 8, message = "Password minimal 8 karakter")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@_#\\-$]).*$",
            message = "Password minimal 1 angka, 1 huruf kecil, 1 huruf besar, 1 karakter seperti @_#-$"
    )
    private String password;

    @NotNull(message = "Nomor HP tidak boleh null")
    @Pattern(
            regexp = "^(62|\\+62|0)8[0-9]{9,13}$",
            message = "Nomor HP tidak valid (9-13 angka setelah 8), contoh: (0/62/+62)81281910384)"
    )
    @JsonProperty("phone-number")
    private String phoneNumber;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
