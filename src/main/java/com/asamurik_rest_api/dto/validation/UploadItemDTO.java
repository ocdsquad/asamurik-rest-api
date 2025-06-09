package com.asamurik_rest_api.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class UploadItemDTO {
//    @NotNull(message = "Item ID tidak boleh kosong")
//    @JsonProperty("item-id")
//    private UUID id;

    @Size(min = 3, max = 100, message = "Nama harus antara 3-100 karakter")
    private String name;

    @Size(min = 20, max = 255, message = "Deskripsi harus antara 20-255 karakter")
    private String description;

    @Size(min = 20, max = 255, message = "Kronologi harus antara 20-255 karakter")
    @JsonProperty("chronology")
    private String chronology;

    @Pattern(regexp = "^(FRESH|ON_PROGRESS|FOUND)$",
            message = "Status hanya boleh (FRESH, ON_PROGRESS, atau FOUND)")
    private String status;

    @Positive(message = "ID kategori harus positif")
    @JsonProperty("category-id")
    private Long categoryId;

    @JsonProperty("user-id")
    private UUID userId;

    @Size(min = 5, max = 255, message = "Lokasi harus antara 5-255 karakter")
    private String location;

//    @NotNull(message = "Gambar harus diupload")
//    @JsonProperty("image-url")
//    private MultipartFile imageUrl;

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
//
//    public MultipartFile getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(MultipartFile imageUrl) {
//        this.imageUrl = imageUrl;
//    }
}

