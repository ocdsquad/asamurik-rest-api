package com.asamurik_rest_api.controller;


import com.asamurik_rest_api.dto.validation.ValidateReportDTO;
import com.asamurik_rest_api.dto.validation.ValidateReportGuestDTO;
import com.asamurik_rest_api.service.ReportService;
import com.asamurik_rest_api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/{itemID}")
    public ResponseEntity<Object> sendReport(@Valid @RequestBody ValidateReportDTO reportDTO,
                                             @RequestHeader(value = "Authorization", required = false) String token, HttpServletRequest request, @PathVariable String itemID) {
        if (token != null && jwtUtil.validateToken(token)) {
//            String userID = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            logger.debug("Username from token: {}", username);
            return reportService.sendReportWithToken(reportService.mapToReport(reportDTO), request, itemID, username);
        } else {
            logger.debug("Token is null or invalid, proceeding without token.");
            return reportService.prepareSendReportWithoutToken(reportDTO, request, itemID);
        }

    }

    @PostMapping("/{itemID}/verify-and-send")
    public ResponseEntity<Object> verifyAndSendReport(@Valid @RequestBody ValidateReportGuestDTO reportDTO,
                                                      HttpServletRequest request, @PathVariable String itemID) {
        logger.debug("Verifying and sending report without token for itemID: {}", itemID);
        logger.debug("Report details: {}", reportDTO);
        return reportService.verifySendReportWithoutToken(reportDTO, request, itemID);
    }
}
