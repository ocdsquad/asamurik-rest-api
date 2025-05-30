package com.asamurik_rest_api.service;

import com.asamurik_rest_api.core.IService;
import com.asamurik_rest_api.dto.representation.RepresentationItemDTO;
import com.asamurik_rest_api.dto.response.ResponseItemDTO;
import com.asamurik_rest_api.dto.update.UpdateItemDTO;
import com.asamurik_rest_api.dto.validation.ValidateItemDTO;
import com.asamurik_rest_api.entity.Category;
import com.asamurik_rest_api.entity.Item;
import com.asamurik_rest_api.entity.ItemStatus;
import com.asamurik_rest_api.entity.User;
import com.asamurik_rest_api.handler.GlobalErrorHandler;
import com.asamurik_rest_api.handler.GlobalSuccessHandler;
import com.asamurik_rest_api.repository.CategoryRepository;
import com.asamurik_rest_api.repository.ItemRepository;
import com.asamurik_rest_api.repository.UserRepository;
import com.asamurik_rest_api.utils.TransformPagination;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ItemRepository itemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransformPagination tp;

    @Override
    public ResponseEntity<Object> save(Item item, HttpServletRequest request) {
        try {
            if (item == null) {
                return GlobalErrorHandler.dataTidakDitemukan("ITEM_NULL", request);
            }

            if (item.getCategoryId() == null || item.getUserId() == null) {
                return GlobalErrorHandler.dataTidakDitemukan("CATEGORY_OR_USER_NULL", request);
            }

            // Validasi Category dan dapatkan entity-nya
            Category category = categoryRepository.findById(item.getCategoryId().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Kategori tidak ditemukan"));

            // Validasi User dan dapatkan entity-nya
            User user = userRepository.findById(item.getUserId().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

            // Set relasi lengkap
            item.setCategoryId(category);
            item.setUserId(user);
            // Set createdBy dengan user yang membuat item
            item.setCreatedBy(user.getId().toString());

            if (item.getCreatedAt() == null) {
                item.setCreatedAt(LocalDateTime.now());
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

    @Override
    public ResponseEntity<Object> update(UUID userId, Item item, HttpServletRequest request) {
        try {
            UUID itemId = item.getId();
            Item existingItem = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item dengan ID " + itemId + " tidak ditemukan"));

            if (item.getName() != null) existingItem.setName(item.getName());
            if (item.getDescription() != null) existingItem.setDescription(item.getDescription());
            if (item.getChronology() != null) existingItem.setChronology(item.getChronology());
            if (item.getLocation() != null) existingItem.setLocation(item.getLocation());
            if (item.getImageUrl() != null) existingItem.setImageUrl(item.getImageUrl());
            if (item.getStatus() != null) existingItem.setStatus(item.getStatus());
            if (item.getCategoryId() != null) existingItem.setCategoryId(item.getCategoryId());
            if (item.getUserId() != null) existingItem.setUserId(item.getUserId());

            existingItem.setUpdatedAt(LocalDateTime.now());
            existingItem.setUpdatedBy(userId.toString());

            itemRepository.save(existingItem);

            return GlobalSuccessHandler.dataBerhasilDisimpan("ITEM_UPDATED", request);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.terjadiKesalahan("ERROR_UPDATE_ITEM", request);
        }
    }

    @Override
    public ResponseEntity<Object> delete(UUID id, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findById(UUID id, HttpServletRequest request) {
        try {
            Optional<Item> optionalItem = itemRepository.findById(id);

            if (optionalItem.isEmpty()) {
                return GlobalErrorHandler.objectIsNull("ITEM_NOT_EXIST", request);
            }

            Item item = optionalItem.get();

            List<ResponseItemDTO> dtoList = List.of(mapToDTO(item));

            return GlobalSuccessHandler.dataDitemukan(dtoList, request);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalErrorHandler.dataTidakDitemukan("FIND_ITEM_FAILED", request);
        }
    }

    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        return null;
    }

    public ResponseEntity<Object> findAllWithFilter(String statusStr, Long categoryId, Pageable pageable, HttpServletRequest request) {
        try {
            ItemStatus status = null;
            if (statusStr != null && !statusStr.isBlank()) {
                try {
                    status = ItemStatus.valueOf(statusStr.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    return GlobalErrorHandler.dataTidakDitemukan("INVALID_STATUS", request);
                }
            }

            Page<Item> page;

            if (status != null && categoryId != null) {
                page = itemRepository.findByStatusAndCategoryId(status, categoryId, pageable);
            } else if (status != null) {
                page = itemRepository.findByStatus(status, pageable);
            } else if (categoryId != null) {
                page = itemRepository.findByCategoryId(categoryId, pageable);
            } else {
                page = itemRepository.findAll(pageable);
            }

            if (page.isEmpty()) {
                return GlobalErrorHandler.dataTidakDitemukan("PAGE_NOT_FOUND", request);
            }

            List<RepresentationItemDTO> listDTO = mapToRepresentationDTO(page.getContent());
            Map<String, Object> data = tp.transformPagination(listDTO, page, "id", "");
            return GlobalSuccessHandler.dataDitemukan(data, request);

        } catch (Exception e) {
            // Optional: Log error e di sini untuk debugging
            return GlobalErrorHandler.terjadiKesalahan("ERROR_FIND_ALL", request);
        }
    }

    public RepresentationItemDTO mapToRepresentationDTO(Item item) {
        RepresentationItemDTO dto = new RepresentationItemDTO();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setImageUrl(item.getImageUrl());

        if (item.getStatus() != null) {
            dto.setStatus(item.getStatus().name());
        }

        if (item.getCategoryId() != null) {
            dto.setCategoryId(item.getCategoryId().getId());
        }

        if (item.getUserId() != null) {
            dto.setUserId(item.getUserId().getId());
        }

        return dto;
    }

    public List<RepresentationItemDTO> mapToRepresentationDTO(List<Item> items) {
        return items.stream()
                .map(this::mapToRepresentationDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, String columnName, String value, HttpServletRequest request) {
        return null;
    }


    private ResponseItemDTO mapToDTO(Item item) {
        ResponseItemDTO dto = new ResponseItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setChronology(item.getChronology());
        dto.setLocation(item.getLocation());
        dto.setImageUrl(item.getImageUrl());
        dto.setCategoryId(item.getCategoryId() != null ? item.getCategoryId().getId() : null);
        dto.setUserId(item.getUserId() != null ? item.getUserId().getId() : null);
        dto.setStatus(item.getStatus() != null ? item.getStatus().name() : null);
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }


    public Item mapToItem(UpdateItemDTO dto) throws BadRequestException {
        Item item = new Item();

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getChronology() != null) item.setChronology(dto.getChronology());
        if (dto.getLocation() != null) item.setLocation(dto.getLocation());
        if (dto.getImageUrl() != null) item.setImageUrl(dto.getImageUrl());

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

    public Item mapToItem(ValidateItemDTO dto) throws BadRequestException {
        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setChronology(dto.getChronology());
        item.setLocation(dto.getLocation());
        item.setImageUrl(dto.getImageUrl());

        try {
            item.setStatus(ItemStatus.valueOf(dto.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Status tidak valid: " + dto.getStatus());
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


//    HARD DELETE
//    public ResponseEntity<Object> deleteById(UUID userId, UUID itemId, HttpServletRequest request) {
//        try {
//            Optional<Item> itemOptional = itemRepository.findById(itemId);
//            if (itemOptional.isEmpty()) {
//                return GlobalErrorHandler.objectIsNull("ITEM_NOT_EXIST", request);
//            }
//
//            Item item = itemOptional.get();
//
//            if (item.getUserId() == null || !item.getUserId().getId().equals(userId)) {
//                return GlobalErrorHandler.aksesDitolak("UNAUTHORIZED_DELETE", request);
//            }
//
//            itemRepository.deleteById(itemId);
//            return GlobalSuccessHandler.dataBerhasilDihapus(request);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return GlobalErrorHandler.dataGagalDihapus("ITEM_DELETE_FAILED", request);
//        }
//    }

}



