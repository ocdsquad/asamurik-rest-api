package com.example.asamurik_rest_api.service;

import com.example.asamurik_rest_api.common.response.ErrorCode;
import com.example.asamurik_rest_api.core.IService;
import com.example.asamurik_rest_api.dto.validation.ValidateReportDTO;
import com.example.asamurik_rest_api.entity.Item;
import com.example.asamurik_rest_api.entity.Report;
import com.example.asamurik_rest_api.entity.User;
import com.example.asamurik_rest_api.handler.ResponseHandler;
import com.example.asamurik_rest_api.repository.ItemRepository;
import com.example.asamurik_rest_api.repository.ReportRepository;
import com.example.asamurik_rest_api.repository.UserRepository;
import com.example.asamurik_rest_api.utils.SendMailUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ResponseEntity<Object> save(Report report, HttpServletRequest request, String itemID, String userID) {
        // Implementation for saving a report

        try {
            UUID userUuid = UUID.fromString(userID);
            UUID itemUuid = UUID.fromString(itemID);

            User user = userRepository.findById(userUuid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userID));
            Item item = itemRepository.findById(itemUuid)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemID));

            report.setItem(item);
            report.setUser(user);
            Report savedReport = reportRepository.save(report);

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

}
