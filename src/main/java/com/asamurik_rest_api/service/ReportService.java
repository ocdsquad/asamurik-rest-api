package com.asamurik_rest_api.service;

import com.asamurik_rest_api.common.response.ErrorCode;
import com.asamurik_rest_api.core.IService;
import com.asamurik_rest_api.dto.response.OTPResponse;
import com.asamurik_rest_api.dto.validation.ValidateReportDTO;
import com.asamurik_rest_api.dto.validation.ValidateReportGuestDTO;
import com.asamurik_rest_api.entity.Item;
import com.asamurik_rest_api.entity.Report;
import com.asamurik_rest_api.entity.User;
import com.asamurik_rest_api.handler.ResponseHandler;
import com.asamurik_rest_api.repository.ItemRepository;
import com.asamurik_rest_api.repository.ReportRepository;
import com.asamurik_rest_api.repository.UserRepository;
import com.asamurik_rest_api.security.BcryptImpl;
import com.asamurik_rest_api.utils.OtpGenerator;
import com.asamurik_rest_api.utils.SendMailUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@Transactional
public class ReportService implements IService<Report, Long> {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public ResponseEntity<Object> save(Report report, HttpServletRequest request) {
        // Implementation for saving a report
        return null;
    }

    public ResponseEntity<Object> sendReportWithToken(Report report, HttpServletRequest request, String itemID, String username) {
        // Implementation for saving a report
        try {
//            UUID userUuid = UUID.fromString(userID);
            UUID itemUuid = UUID.fromString(itemID);


            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + username));
            Item item = itemRepository.findById(itemUuid)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemID));

            report.setItem(item);
            report.setUser(user);
            reportRepository.save(report);

            SendMailUtil.sendEmail(
                    user.getFullname(),
                    report.getMessage(),
                    report.getItem().getUserId().getEmail()
            );



            Thread.sleep(1000);

        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    e.getMessage(),
                    request
            );
        }
        return new ResponseHandler().handleResponse(
                "Report send successfully to " + report.getItem().getUserId().getFullname(),
                HttpStatus.CREATED,
                null,
                null,
                request
        );
    }

    public ResponseEntity<Object> prepareSendReportWithoutToken(ValidateReportDTO report, HttpServletRequest request, String itemID) {
        // Implementation for saving a report
        Map<String, Object> response = new HashMap<>();
        try {

            String otp = OtpGenerator.generateOtp();
            User user = userRepository.findByEmail(report.getEmail()).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(report.getEmail());
                newUser.setFullname(report.getFullname());
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setOtp(BcryptImpl.hash(otp));
                return userRepository.save(newUser);
            });


            SendMailUtil.sendOTP(
                    "OTP Verifikasi User",
                    user.getFullname(),
                    user.getEmail(),
                    otp,
                    "ver_otp.html"
            );
            Thread.sleep(1000);


            response.put("otp", otp);
            OTPResponse otpResponse = mapToOTPResponseDTO(user);

            return new ResponseHandler().handleResponse(
                    "Silahkan masukkan OTP yang telah dikirim ke email anda untuk mengirim laporan",
                    HttpStatus.CREATED,
                    response,
                    null,
                    request
            );

        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    e.getMessage(),
                    request
            );
        }


    }

    public ResponseEntity<Object> verifySendReportWithoutToken(ValidateReportGuestDTO reportDTO, HttpServletRequest request, String itemID) {
        Report report = new Report();
        try {
            User user = userRepository.findByEmail(reportDTO.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + reportDTO.getEmail()));

            if (!BcryptImpl.verifyHash(reportDTO.getOtp(), user.getOtp())) {
                return new ResponseHandler().handleResponse(
                        "Invalid OTP",
                        HttpStatus.UNAUTHORIZED,
                        null,
                        null,
                        request
                );
            }

            UUID itemUuid = UUID.fromString(itemID);
            Item item = itemRepository.findById(itemUuid)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemID));


            report.setUser(user);
            report.setItem(item);
            report.setMessage(reportDTO.getMessage());

            reportRepository.save(report);

            SendMailUtil.sendEmail(
                    user.getFullname(),
                    report.getMessage(),
                    report.getItem().getUserId().getEmail()
            );

            Thread.sleep(1000);

        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    e.getMessage(),
                    request
            );
        }

        return new ResponseHandler().handleResponse(
                "Report send successfully to " + report.getItem().getUserId().getFullname(),
                HttpStatus.CREATED,
                null,
                null,
                request
        );
    }


    @Override
    public ResponseEntity<Object> update(Long id, Report report, HttpServletRequest request) {
        // Implementation for updating a report
        return null;
    }


    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        // Implementation for deleting a report
        return null;
    }

    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        // Implementation for finding all reports
        return null;
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        // Implementation for finding a report by ID
        return null;
    }

    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, String columnName, String value, HttpServletRequest request) {
        // Implementation for finding reports by parameter
        return null;
    }

    public Report mapToReport(ValidateReportDTO reportDTO) {
        return modelMapper.map(reportDTO, Report.class);
    }

    public OTPResponse mapToOTPResponseDTO(User user) {
        return modelMapper.map(user, OTPResponse.class);
    }

}
