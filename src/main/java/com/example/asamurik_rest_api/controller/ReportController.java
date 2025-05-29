package com.example.asamurik_rest_api.controller;


import com.example.asamurik_rest_api.dto.validation.ValidateReportDTO;
import com.example.asamurik_rest_api.dto.validation.ValidateReportGuestDTO;
import com.example.asamurik_rest_api.service.ReportService;
import com.example.asamurik_rest_api.utils.JwtUtil;
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
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/{itemID}")
    public ResponseEntity<Object> sendReport(@Valid @RequestBody ValidateReportDTO reportDTO,
            @RequestHeader("Authorization") String token, HttpServletRequest request, @PathVariable String itemID) {
        if(token != null && jwtUtil.validateToken(token)){
            String userID = jwtUtil.getUserIdFromToken(token);
            return reportService.sendReportWithToken(reportService.mapToReport(reportDTO), request, itemID, userID);
        }else{
            return reportService.prepareSendReportWithoutToken(reportService.mapToReport(reportDTO), request, itemID);
        }
        
    }

    @PostMapping("/{itemID}/verify-and-send")
    public ResponseEntity<Object> verifyAndSendReport(@Valid @RequestBody ValidateReportGuestDTO reportDTO,
                                                      HttpServletRequest request, @PathVariable String itemID) {

        return reportService.verifySendReportWithoutToken(reportDTO, request, itemID);
    }
}
