package com.asamurik_rest_api.repository;

import com.asamurik_rest_api.entity.Item;
import com.asamurik_rest_api.entity.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Item> findByStatus(ItemStatus status, Pageable pageable);

    Page<Item> findByStatusAndCategoryId(ItemStatus status, Long categoryId, Pageable pageable);
}
