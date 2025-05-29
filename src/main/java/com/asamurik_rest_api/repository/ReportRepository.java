package com.asamurik_rest_api.repository;

import com.asamurik_rest_api.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Custom query methods can be defined here if needed
    // For example, to find reports by a specific user or status
    List<Report> findByUserId(UUID userId);
    // List<Report> findByStatus(String status);
}
