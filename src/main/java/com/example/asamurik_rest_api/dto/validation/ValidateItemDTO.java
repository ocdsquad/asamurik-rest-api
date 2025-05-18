package com.example.asamurik_rest_api.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ValidateItemDTO {
    @NotBlank(message = "Nama barang tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama harus antara 3-100 karakter")
    private String name;

    @NotBlank(message = "Deskripsi barang tidak boleh kosong")
    @Size(min = 20, max = 255, message = "Deskripsi harus antara 20-255")
    private String description;

    @NotBlank(message = "Kronologi tidak boleh kosong")
    @Size(min = 20, max = 255, message = "Kronologi harus antara 20-255 karakter")
    private String chronology;

    @NotNull(message = "Status barang tidak boleh null")
    @Pattern(
            regexp = "^(Fresh|In Progress|Found)$",
            message = "Status hanya boleh (Fresh, In Progress, atau Found)"
    )
    private String status;

    @NotNull(message = "ID kategori tidak boleh null")
    @Positive(message = "ID kategori harus positif")
    @JsonProperty("category-id")
    private Long categoryId;

    @NotBlank(message = "Lokasi tidak boleh kosong")
    @Size(min = 5, max = 255, message = "Lokasi harus antara 5-255 karakter")
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChronology() {
        return chronology;
    }

    public void setChronology(String chronology) {
        this.chronology = chronology;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
