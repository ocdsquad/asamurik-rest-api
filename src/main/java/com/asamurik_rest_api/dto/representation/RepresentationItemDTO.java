package com.asamurik_rest_api.dto.representation;

/*
IntelliJ IDEA 2024.3.5 (Community Edition)
Build #IC-243.26053.27, built on March 16, 2025
@Author Rayhan a.k.a. M Rayhan Putra Thahir
Java Developer
Created on 28/05/2025 06:45
@Last Modified 28/05/2025 06:45
Version 1.0
*/

import java.time.LocalDateTime;

import java.util.UUID;

//Output ke client saat menampilkan detail barang.
//Berisi field lengkap yang user perlu tahu (termasuk info relasi seperti nama kategori dan pelapor, tanggal, dll).

public class RepresentationItemDTO {
    private UUID id;
    private String name;
    private String imageUrl;
    private Long categoryId;
    private UUID userId;
    private String status;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
