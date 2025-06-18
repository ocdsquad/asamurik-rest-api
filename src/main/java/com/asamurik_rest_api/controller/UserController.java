package com.asamurik_rest_api.controller;

import com.asamurik_rest_api.common.response.ErrorCode;
import com.asamurik_rest_api.dto.validation.ValidateUpdateUserDTO;
import com.asamurik_rest_api.handler.ResponseHandler;
import com.asamurik_rest_api.service.UserService;
import com.asamurik_rest_api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfileById(@RequestHeader("Authorization") String token, HttpServletRequest request) {
        logger.debug("Received token: {}", token);
        if (token != null && jwtUtil.validateToken(token)) {
            UUID userID = UUID.fromString(jwtUtil.getUserIdFromToken(token));
            logger.debug("Extracted user UUID: {}", userID);

            return userService.findById(userID, request);
        }

        return new ResponseHandler().handleResponse(
                ErrorCode.UNAUTHORIZED.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null,
                null,
                request
        );

    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateUserProfile(
            @Valid @RequestPart("user") ValidateUpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "image-url", required = false) MultipartFile file,
            HttpServletRequest request
    ) {
        logger.debug("Received file: {}", file != null ? file.getOriginalFilename() : "null");
        logger.debug("Received updateUserDTO: {}", updateUserDTO);
        try {
            if (token != null && jwtUtil.validateToken(token)) {
            UUID userId = UUID.fromString(jwtUtil.getUserIdFromToken(token));
                logger.debug("Extracted username: {}", userId);

                //TODO: Update this to use the correct ID from the JWT claims
                return userService.updateById(userId, file, userService.mapToUser(updateUserDTO), request);
            } else {
                return new ResponseHandler().handleResponse(
                        ErrorCode.UNAUTHORIZED.getMessage(),
                        HttpStatus.UNAUTHORIZED,
                        null,
                        null,
                        request
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
