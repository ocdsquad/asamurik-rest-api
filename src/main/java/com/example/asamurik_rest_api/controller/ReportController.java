package com.example.asamurik_rest_api.controller;


import com.example.asamurik_rest_api.dto.validation.ValidateReportDTO;
import com.example.asamurik_rest_api.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("/{itemID}")
    public ResponseEntity<Object> createReport(@Valid @RequestBody ValidateReportDTO reportDTO,
                                               HttpServletRequest request, @PathVariable String itemID) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer " prefix
        String userID = "jwtTokenUtil.validateAndExtractUserId(token)";
        return reportService.save(reportService.mapToReport(reportDTO), request, itemID, userID);
    }
}
