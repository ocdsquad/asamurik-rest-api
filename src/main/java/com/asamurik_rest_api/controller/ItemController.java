package com.asamurik_rest_api.controller;

import com.asamurik_rest_api.dto.validation.UploadItemDTO;
import com.asamurik_rest_api.dto.validation.ValidateItemDTO;
import com.asamurik_rest_api.entity.Item;
import com.asamurik_rest_api.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


// ItemController.java
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> saveItem(
            @Valid @RequestPart("item") ValidateItemDTO validateItemDTO,
            @RequestPart("image-url") MultipartFile imageFile,
            HttpServletRequest request) throws BadRequestException {
        if (imageFile != null) {
            System.out.println("File original name: " + imageFile.getOriginalFilename());
            System.out.println("File content type: " + imageFile.getContentType());
            System.out.println("File size: " + imageFile.getSize());
        } else {
            System.out.println("No file uploaded for 'image-url'");
        }


        Item item = itemService.mapToSaveItem(validateItemDTO);
        return itemService.saveItem(item, imageFile, request);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateItem(
            @PathVariable("id") UUID itemId,
            @Valid @RequestPart("item") UploadItemDTO uploadItemDTO,
            @RequestPart(value = "image-url", required = false) MultipartFile imageFile,
            HttpServletRequest request,
            @RequestHeader("userId") UUID userId) throws BadRequestException {

        Item itemUpdate = itemService.mapToUploadItem(uploadItemDTO);
        return itemService.updateItem(userId, itemId, itemUpdate, imageFile, request);
    }


    // Cari item by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> findByItemId(
            @PathVariable("id") UUID itemId,
            HttpServletRequest request) {
        return itemService.findByItemId(itemId, request);
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<Object> findByUserId(
            @PathVariable("id") UUID userId,
            HttpServletRequest request) {
        return itemService.findByUserId(userId, request);
    }


    @GetMapping
    public ResponseEntity<Object> findByParam(
            @RequestParam(required = false) String status,
            @RequestParam(name = "category-id", required = false) Long categoryId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            HttpServletRequest request
    ) {
        // Default sorting
        String sortParam = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort;

        Sort sortObj;
        if (sortParam.contains(",")) {
            String[] sortParts = sortParam.split(",");
            sortObj = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        } else {
            sortObj = Sort.by(Sort.Direction.fromString(sortParam), "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Sanitasi input
        String statusFilter = (status == null || status.isBlank()) ? null : status;
        Long categoryFilter = categoryId;
        String nameFilter = (name == null || name.isBlank()) ? null : name;

        return itemService.findByParam(
                statusFilter,
                categoryFilter,
                nameFilter,
                pageable,
                request
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> softDeleteItem(
            @PathVariable("id") UUID itemId,
            @RequestHeader("userId") UUID userId,
            HttpServletRequest request) {
        return itemService.softDeleteItem(itemId, userId, request);
    }
}




