package com.asamurik_rest_api.repository;

import com.asamurik_rest_api.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Custom query methods can be defined here if needed
    // For example, to find reports by a specific user or status
    List<Report> findByUserId(UUID userId);
    // List<Report> findByStatus(String status);

    @Query("SELECT r FROM Report r WHERE r.item.id = :itemId AND r.deletedAt IS NULL")
    List<Report> findActiveReportsByItemId(@Param("itemId") UUID itemId);

    @Query("SELECT r FROM Report r WHERE r.item.id = :itemId")
    List<Report> findByItemId(@Param("itemId") UUID itemId);



}
