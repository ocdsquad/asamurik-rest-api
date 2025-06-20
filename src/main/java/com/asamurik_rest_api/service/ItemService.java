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
import com.asamurik_rest_api.utils.FileValidatorUtil;
import com.asamurik_rest_api.utils.JwtUtil;
import com.asamurik_rest_api.utils.TransformPagination;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

    @Autowired
    private Cloudinary cloudinary;

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

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

        return item;
    }


    public ResponseEntity<Object> findByItemId(UUID itemId, HttpServletRequest request) {
        try {
            Optional<Item> optionalItem = itemRepository.findByItemId(itemId);

            if (optionalItem.isEmpty()) {
                return GlobalErrorHandler.dataTidakDitemukan("ITEM NOT FOUND", request);
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
            logger.info("Received status filter: " + statusStr);


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
                return GlobalErrorHandler.dataTidakDitemukan("ITEM NULL", request);
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
                    throw new IllegalArgumentException("masukkan tipe file gambar yang valid");
                }

                if (!FileValidatorUtil.isValidFileSize(imageFile.getSize(), MAX_FILE_SIZE)) {
                    throw new IllegalArgumentException("file gambar melebihi batas ukuran maksimum 5MB");
                }

                String imageUrl = uploadImage(imageFile);
//                String uploadedPath = FileStorageUtil.saveFile(imageFile, TEMP_IMAGE_DIR);
                item.setImageUrl(imageUrl);
            }

            itemRepository.save(item);
            return GlobalSuccessHandler.dataBerhasilDisimpan("ITEM SAVED", request);
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

                String imageUrl = uploadImage(imageFile);
                deleteImage(existingItem); // Hapus gambar lama jika ada
//                String uploadedPath = FileStorageUtil.saveFile(imageFile, TEMP_IMAGE_DIR);
                existingItem.setImageUrl(imageUrl);
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
            return GlobalSuccessHandler.dataBerhasilDisimpan("ITEM UPDATED", request);
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
                return GlobalErrorHandler.dataTidakDitemukan("token tidak valid", request);
            }

            String token = authHeader.substring(7);
            UUID userId = UUID.fromString(jwtTokenUtil.getUserIdFromToken(token));

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("item tidak ditemukan"));

            if (item.getDeletedAt() != null) {
                return GlobalErrorHandler.dataTidakDitemukan("item sudah dihapus", request);
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
            return GlobalErrorHandler.terjadiKesalahan("ERROR FINDING USER", request);
        }
    }

    private String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "public_id", "item_images/" + UUID.randomUUID(),
                "overwrite", true
        ));
        String imageUrl = uploadResult.get("secure_url").toString();
        logger.debug("Image URL: {}", imageUrl);
        return imageUrl;
    }

//    private void deleteImage(Item updatedItem) throws IOException {
//        if (updatedItem.getImageUrl() != null) {
//            Map destroyResult = cloudinary.uploader().destroy(extractPublicId(updatedItem.getImageUrl()), ObjectUtils.emptyMap());
//            logger.debug("Image destroyed: {}", destroyResult.get("result"));
//        }
//    }

//    public String extractPublicId(String imageUrl) {
//        String base = "/upload/";
//        int index = imageUrl.indexOf(base);
//        if (index == -1) return null;
//
//        String path = imageUrl.substring(index + base.length());
//        if (path.startsWith("v") && path.contains("/")) {
//            path = path.substring(path.indexOf("/") + 1);
//        }
//
//        int lastDot = path.lastIndexOf('.');
//        if (lastDot != -1) {
//            path = path.substring(0, lastDot);
//        }
//        return path;
//    }


    private void deleteImage(Item updatedItem) throws IOException {
        String publicId = extractPublicId(updatedItem.getImageUrl());
        logger.debug("Extracted public_id: {}", publicId); // Tampilkan hasil public_id di log

        if (publicId != null) {
            Map destroyResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.debug("Image destroyed: {}", destroyResult.get("result")); // Lihat hasil penghapusan di log
        } else {
            logger.warn("Public ID could not be extracted, image not deleted.");
        }
    }


    public String extractPublicId(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath(); // contoh hasil: /yourcloud/image/upload/v1718800000/item_images/abc123.jpg

            // Ambil bagian setelah "/upload/"
            String[] segments = path.split("/upload/");
            if (segments.length < 2) return null;

            String uploadPath = segments[1]; // "v1718800000/item_images/abc123.jpg"

            // Hilangkan versi (misal: v1718800000/)
            if (uploadPath.startsWith("v")) {
                uploadPath = uploadPath.substring(uploadPath.indexOf("/") + 1);
            }

            // Hapus ekstensi .jpg atau lainnya
            int lastDot = uploadPath.lastIndexOf('.');
            if (lastDot != -1) {
                uploadPath = uploadPath.substring(0, lastDot);
            }

            return uploadPath; // hasil akhir: "item_images/abc123"
        } catch (URISyntaxException e) {
            logger.error("Invalid image URL: {}", imageUrl);
            return null;
        }
    }

}
