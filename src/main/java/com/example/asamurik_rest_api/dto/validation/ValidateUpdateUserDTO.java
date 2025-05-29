package com.example.asamurik_rest_api.dto.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ValidateUpdateUserDTO {

    @NotNull(message = "Nama lengkap tidak boleh null")
    @Size(min = 4, max = 100, message = "Nama lengkap harus antara 4-100 karakter")
    @Pattern(
            regexp = "^[a-zA-Z\\s]*$",
            message = "Nama lengkap hanya boleh huruf dan spasi, contoh: Reza Pangestu"
    )
    private String fullname;


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
