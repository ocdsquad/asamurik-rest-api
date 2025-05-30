package com.asamurik_rest_api.controller;

/*
IntelliJ IDEA 2024.3.5 (Community Edition)
Build #IC-243.26053.27, built on March 16, 2025
@Author Rayhan a.k.a. M Rayhan Putra Thahir
Java Developer
Created on 27/05/2025 18:26
@Last Modified 27/05/2025 18:26
Version 1.0
*/

import com.asamurik_rest_api.dto.ItemDTO;
import com.asamurik_rest_api.dto.update.UpdateItemDTO;
import com.asamurik_rest_api.dto.validation.ValidateItemDTO;
import com.asamurik_rest_api.entity.Category;
import com.asamurik_rest_api.entity.Item;
import com.asamurik_rest_api.entity.User;
import com.asamurik_rest_api.handler.GlobalErrorHandler;
import com.asamurik_rest_api.repository.ItemRepository;
import com.asamurik_rest_api.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


// ItemController.java
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ItemRepository itemRepository;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody ValidateItemDTO validateItemDTO,
                                       HttpServletRequest request) throws BadRequestException {
        // mapping DTO ke entity di service, lalu panggil save yang terima entity
        return itemService.save(itemService.mapToItem(validateItemDTO), request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateItem(
            @PathVariable("id") UUID itemId,
            @RequestBody UpdateItemDTO updateItemDTO,
            HttpServletRequest request,
            @RequestHeader("userId") UUID userId) throws BadRequestException {

        Item item = itemService.mapToItem(updateItemDTO);

        item.setId(itemId);

        return itemService.update(userId, item, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @PathVariable("id") UUID id,
            HttpServletRequest request) {
        // Panggil service findById
        return itemService.findById(id, request);
    }

//    HARD DELETE
//    @DeleteMapping("/{itemId}")
//    public ResponseEntity<Object> deleteById(
//            @PathVariable("itemId") UUID itemId,
//            @RequestHeader("userId") UUID userId,
//            HttpServletRequest request) throws BadRequestException {
//
//        return itemService.deleteById(userId, itemId, request);
//
//    }

    @GetMapping
    public ResponseEntity<Object> findAllWithFilter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "desc") String sortDir, // default descending
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException e) {
            // jika input tidak valid, fallback ke DESC
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        return itemService.findAllWithFilter(status, categoryId, pageable, request);
    }



}

//    @GetMapping
//    public ResponseEntity<Object> getItems(
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) Long categoryId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir,
//            HttpServletRequest request
//    ) {
//        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//
//        return itemService.findAllWithFilter(status, categoryId, pageable, request);
//    }





