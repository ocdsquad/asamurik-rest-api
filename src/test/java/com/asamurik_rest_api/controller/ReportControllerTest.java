package com.asamurik_rest_api.controller;

import com.asamurik_rest_api.dto.validation.ValidateReportDTO;
import com.asamurik_rest_api.dto.validation.ValidateReportGuestDTO;
import com.asamurik_rest_api.service.ReportService;
import com.asamurik_rest_api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test(priority = 110)
    public void testSendReport_WithValidToken() {
        String token = "validToken";
        String itemID = "item123";
        ValidateReportDTO reportDTO = new ValidateReportDTO();
        reportDTO.setFullname("John Doe");
        reportDTO.setEmail("johndoe@example.com");
        reportDTO.setMessage("Test report message");

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("johndoe");
        when(reportService.sendReportWithToken(any(), eq(request), eq(itemID), eq("johndoe")))
                .thenReturn(ResponseEntity.ok("Report sent successfully"));

        ResponseEntity<Object> response = reportController.sendReport(reportDTO, token, request, itemID);
        
        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(response.getBody(), "Report sent successfully");

        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(reportService).sendReportWithToken(any(), eq(request), eq(itemID), eq("johndoe"));
    }

    @Test(priority = 120)
    public void testSendReport_WithoutToken() {
        String token = null;
        String itemID = "item123";
        ValidateReportDTO reportDTO = new ValidateReportDTO();
        reportDTO.setFullname("John Doe");
        reportDTO.setEmail("johndoe@example.com");
        reportDTO.setMessage("Test report message");

        when(reportService.prepareSendReportWithoutToken(eq(reportDTO), eq(request), eq(itemID)))
                .thenReturn(ResponseEntity.ok("OTP sent successfully"));

        ResponseEntity<Object> response = reportController.sendReport(reportDTO, token, request, itemID);

        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(response.getBody(), "OTP sent successfully");

        verify(reportService).prepareSendReportWithoutToken(eq(reportDTO), eq(request), eq(itemID));
    }

    @Test(priority = 130)
    public void testVerifyAndSendReport() {
        String itemID = "item123";
        ValidateReportGuestDTO reportGuestDTO = new ValidateReportGuestDTO();
        reportGuestDTO.setFullname("John Doe");
        reportGuestDTO.setEmail("johndoe@example.com");
        reportGuestDTO.setMessage("Test report message");
        reportGuestDTO.setOtp("123456");

        when(reportService.verifySendReportWithoutToken(eq(reportGuestDTO), eq(request), eq(itemID)))
                .thenReturn(ResponseEntity.ok("Report verified and sent successfully"));

        ResponseEntity<Object> response = reportController.verifyAndSendReport(reportGuestDTO, request, itemID);

        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(response.getBody(), "Report verified and sent successfully");

        verify(reportService).verifySendReportWithoutToken(eq(reportGuestDTO), eq(request), eq(itemID));
    }
}