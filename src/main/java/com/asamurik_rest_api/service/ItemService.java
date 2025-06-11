package com.asamurik_rest_api.service;

import com.asamurik_rest_api.core.IService;
import com.asamurik_rest_api.dto.response.ResponseItemDTO;
import com.asamurik_rest_api.dto.validation.UploadItemDTO;
import com.asamurik_rest_api.dto.validation.ValidateItemDTO;
import com.asamurik_rest_api.entity.*;
import com.asamurik_rest_api.handler.GlobalErrorHandler;
import com.asamurik_rest_api.handler.GlobalSuccessHandler;
import com.asamurik_rest_api.repository.CategoryRepository;
import com.asamurik_rest_api.repository.ItemRepository;
import com.asamurik_rest_api.repository.ReportRepository;
import com.asamurik_rest_api.repository.UserRepository;
import com.asamurik_rest_api.utils.FileStorageUtil;
import com.asamurik_rest_api.utils.FileValidatorUtil;
import com.asamurik_rest_api.utils.JwtUtil;
import com.asamurik_rest_api.utils.TransformPagination;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
@Transactional
public class ItemService implements IService<Item, UUID> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TransformPagination tp;

    @Autowired
    private JwtUtil jwtTokenUtil;

    public Item mapToUploadItem(UploadItemDTO dto) throws BadRequestException {
        Item item = new Item();

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getChronology() != null) item.setChronology(dto.getChronology());
        if (dto.getLocation() != null) item.setLocation(dto.getLocation());

        if (dto.getStatus() != null) {
            try {
                item.setStatus(ItemStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Status tidak valid: " + dto.getStatus());
            }
        }

        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            item.setCategoryId(category);
        }

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            item.setUserId(user);
        }

        return item;
    }


    // Mapping Item ke ResponseItemDTO secara lengkap
    public ResponseItemDTO mapToResponseDTO(Item item) {
        ResponseItemDTO dto = new ResponseItemDTO();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setChronology(item.getChronology());
        dto.setLocation(item.getLocation());
        dto.setImageUrl(item.getImageUrl());
        dto.setStatus(item.getStatus() != null ? item.getStatus().name() : null);
        dto.setCategoryId(item.getCategoryId() != null ? item.getCategoryId().getId() : null);
        dto.setUserId(item.getUserId() != null ? item.getUserId().getId() : null);
        dto.setCreatedAt(item.getCreatedAt());

        return dto;
    }


    public List<ResponseItemDTO> mapToResponseDTO(List<Item> items) {
        return items.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    public Item mapToSaveItem(ValidateItemDTO dto) throws BadRequestException {
        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setChronology(dto.getChronology());
        item.setLocation(dto.getLocation());

        // Jika dto.getStatus null atau kosong, pakai default FRESH
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            item.setStatus(ItemStatus.FRESH);
        } else {
            try {
                item.setStatus(ItemStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Status tidak valid: " + dto.getStatus());
            }
        }

        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            item.setCategoryId(category);
        }

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            item.setUserId(user);
        }

        return item;
    }


    public ResponseEntity<Object> findByItemId(UUID itemId, HttpServletRequest request) {
        try {
            Optional<Item> optionalItem = itemRepository.findByItemId(itemId);

            if (optionalItem.isEmpty()) {
                return GlobalErrorHandler.dataTidakDitemukan("ITEM_NOT_FOUND", request);
            }

            Item item = optionalItem.get();

            ResponseItemDTO dto = mapToResponseDTO(item);

            return GlobalSuccessHandler.dataDitemukan(dto, request);

        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_FIND_BY_ID", request);
        }
    }


    public ResponseEntity<Object> findByParam(String statusStr, Long categoryId, String namePart, Pageable pageable, HttpServletRequest request) {
        try {
            ItemStatus status = null;
            if (statusStr != null && !statusStr.isBlank()) {
                try {
                    status = ItemStatus.valueOf(statusStr.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    return GlobalErrorHandler.dataTidakDitemukan("INVALID_STATUS", request);
                }
            }

            Page<Item> page = itemRepository.findFiltered(status, categoryId, namePart == null ? null : "%" + namePart.toLowerCase() + "%", pageable);

            if (page.isEmpty()) {
                return GlobalErrorHandler.dataTidakDitemukan("NO_ITEMS_FOUND", request);
            }

            List<ResponseItemDTO> dtos = mapToResponseDTO(page.getContent());
            Map<String, Object> data = tp.transformPagination(dtos, page, "id", "");

            return GlobalSuccessHandler.dataDitemukan(data, request);

        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_FIND_FILTERED", request);
        }
    }


    @Override
    public ResponseEntity<Object> save(Item item, HttpServletRequest request) {
        return null;
    }


    @Override
    public ResponseEntity<Object> update(UUID id, Item item, HttpServletRequest request) {
        return null;
    }


    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, String columnName, String value, HttpServletRequest request) {
        return null;
    }


    @Override
    public ResponseEntity<Object> delete(UUID id, HttpServletRequest request) {return null;}

    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findById(UUID id, HttpServletRequest request) {
        return null;
    }


    public ResponseEntity<Object> saveItem(Item item, MultipartFile imageFile, HttpServletRequest request) {
        try {
            if (item == null) {
                return GlobalErrorHandler.dataTidakDitemukan("ITEM_NULL", request);
            }

            if (item.getCategoryId() == null || item.getCategoryId().getId() == null) {
                return GlobalErrorHandler.dataTidakDitemukan("CATEGORY_ID_NULL", request);
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return GlobalErrorHandler.dataTidakDitemukan("TOKEN_TIDAK_VALID", request);
            }

            String token = authHeader.substring(7);
            UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(token));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

            Category category = categoryRepository.findById(item.getCategoryId().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Kategori tidak ditemukan"));

            item.setCategoryId(category);
            item.setUserId(user);
            item.setCreatedBy(userId.toString());

            if (item.getCreatedAt() == null) {
                item.setCreatedAt(LocalDateTime.now());
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                long MAX_FILE_SIZE = 5 * 1024 * 1024;
                String TEMP_IMAGE_DIR = "uploads/item_images/";

                if (!FileValidatorUtil.isImageFile(imageFile)) {
                    throw new IllegalArgumentException("Invalid image file type");
                }

                if (!FileValidatorUtil.isValidFileSize(imageFile.getSize(), MAX_FILE_SIZE)) {
                    throw new IllegalArgumentException("Image file size exceeds limit");
                }

                String uploadedPath = FileStorageUtil.saveFile(imageFile, TEMP_IMAGE_DIR);
                item.setImageUrl(uploadedPath);
            }

            itemRepository.save(item);
            return GlobalSuccessHandler.dataBerhasilDisimpan("ITEM_SAVED", request);
        } catch (IllegalArgumentException e) {
            return GlobalErrorHandler.dataTidakDitemukan(e.getMessage(), request);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_SAVE_ITEM", request);
        }
    }


    public ResponseEntity<Object> updateItem(UUID itemId, Item itemUpdate, MultipartFile imageFile, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return GlobalErrorHandler.dataTidakDitemukan("TOKEN_TIDAK_VALID", request);
            }

            String token = authHeader.substring(7);
            UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(token));

            Item existingItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item tidak ditemukan"));

            if (imageFile != null && !imageFile.isEmpty()) {
                long MAX_FILE_SIZE = 5 * 1024 * 1024;
                String TEMP_IMAGE_DIR = "uploads/item_images/";

                if (!FileValidatorUtil.isImageFile(imageFile)) {
                    throw new IllegalArgumentException("Invalid image file type");
                }

                if (!FileValidatorUtil.isValidFileSize(imageFile.getSize(), MAX_FILE_SIZE)) {
                    throw new IllegalArgumentException("Image file size exceeds limit");
                }

                String uploadedPath = FileStorageUtil.saveFile(imageFile, TEMP_IMAGE_DIR);
                existingItem.setImageUrl(uploadedPath);
            }

            if (itemUpdate.getName() != null) existingItem.setName(itemUpdate.getName());
            if (itemUpdate.getDescription() != null) existingItem.setDescription(itemUpdate.getDescription());
            if (itemUpdate.getChronology() != null) existingItem.setChronology(itemUpdate.getChronology());
            if (itemUpdate.getLocation() != null) existingItem.setLocation(itemUpdate.getLocation());
            if (itemUpdate.getStatus() != null) existingItem.setStatus(itemUpdate.getStatus());
            if (itemUpdate.getCategoryId() != null) existingItem.setCategoryId(itemUpdate.getCategoryId());

            existingItem.setUpdatedAt(LocalDateTime.now());
            existingItem.setUpdatedBy(userId.toString());

            itemRepository.save(existingItem);
            return GlobalSuccessHandler.dataBerhasilDisimpan("ITEM_UPDATED", request);
        } catch (IllegalArgumentException e) {
            return GlobalErrorHandler.dataTidakDitemukan(e.getMessage(), request);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_UPDATE_ITEM", request);
        }
    }


    public ResponseEntity<Object> softDeleteItem(UUID itemId, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return GlobalErrorHandler.dataTidakDitemukan("TOKEN_TIDAK_VALID", request);
            }

            String token = authHeader.substring(7);
            UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(token));

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item tidak ditemukan"));

            if (item.getDeletedAt() != null) {
                return GlobalErrorHandler.dataTidakDitemukan("Item sudah dihapus", request);
            }

            LocalDateTime now = LocalDateTime.now();
            item.setDeletedAt(now);
            item.setDeletedBy(userId.toString());
            item.setUpdatedAt(now);
            item.setUpdatedBy(userId.toString());

            List<Report> relatedReports = reportRepository.findByItemId(itemId);
            for (Report report : relatedReports) {
                if (report.getDeletedAt() == null) {
                    report.setDeletedAt(now);
                }
            }

            reportRepository.saveAll(relatedReports);
            itemRepository.save(item);

            return GlobalSuccessHandler.dataBerhasilDihapus(request);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_DELETE_ITEM", request);
        }
    }

    public ResponseEntity<Object> findByUserId(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return GlobalErrorHandler.dataTidakDitemukan("TOKEN_TIDAK_VALID", request);
            }

            String token = authHeader.substring(7);
            UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(token));

            List<Item> items = itemRepository.findByUserId(userId);

            if (items.isEmpty()) {
                return GlobalErrorHandler.dataTidakDitemukan("TIDAK_ADA_ITEM_USER", request);
            }

            return GlobalSuccessHandler.dataDitemukan(items, request);
        } catch (IllegalArgumentException e) {
            return GlobalErrorHandler.dataTidakDitemukan(e.getMessage(), request);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_FIND_ITEM_USER", request);
        }
    }



}
