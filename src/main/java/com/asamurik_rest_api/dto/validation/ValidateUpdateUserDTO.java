package com.asamurik_rest_api.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class ValidateUpdateUserDTO {
    @NotNull(message = "Nama lengkap tidak boleh null")
    @Size(min = 4, max = 100, message = "Nama lengkap harus antara 4-100 karakter")
    @Pattern(
            regexp = "^[a-zA-Z\\s]*$",
            message = "Nama lengkap hanya boleh huruf dan spasi, contoh: Reza Pangestu"
    )
    private String fullname;

    @NotNull(message = "Email tidak boleh null")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    @Pattern(
            regexp = "^(?=.{1,64}@.{1,255}$)(?:(?![.])[a-zA-Z0-9._%+-]+(?:(?<!\\\\)[.][a-zA-Z0-9-]+)*?)@[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,50})+$",
            message = "Format email tidak valid, contoh: reza_pangestu123@gmail.com"
    )
    private String email;

    @NotNull(message = "Nomor HP tidak boleh null")
    @Pattern(
            regexp = "^(62|\\+62|0)8[0-9]{9,13}$",
            message = "Nomor HP tidak valid (9-13 angka setelah 8), contoh: (0/62/+62)81281910384)"
    )
    @JsonProperty("phone-number")
    private String phoneNumber;

    @URL(message = "URL gambar tidak valid")
    @JsonProperty("image-url")
    private String imageUrl;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
