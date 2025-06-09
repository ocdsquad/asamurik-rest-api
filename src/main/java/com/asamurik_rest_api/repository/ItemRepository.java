package com.asamurik_rest_api.repository;

import com.asamurik_rest_api.entity.Item;
import com.asamurik_rest_api.entity.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query("""
SELECT i FROM Item i
WHERE (:status IS NULL OR i.status = :status)
AND (:categoryId IS NULL OR i.categoryId.id = :categoryId)
AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(:name))
AND i.deletedAt IS NULL
""")
    Page<Item> findFiltered(
            @Param("status") ItemStatus status,
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT i FROM Item i WHERE i.userId.id = :userId AND i.deletedAt IS NULL")
    List<Item> findByUserId(UUID userId);


    @Query("SELECT i FROM Item i WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<Item> findByItemId(UUID id);

}
