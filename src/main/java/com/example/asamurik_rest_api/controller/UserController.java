package com.example.asamurik_rest_api.controller;

import com.example.asamurik_rest_api.common.response.ErrorCode;
import com.example.asamurik_rest_api.dto.validation.ValidateUpdateUserDTO;
import com.example.asamurik_rest_api.handler.ResponseHandler;
import com.example.asamurik_rest_api.service.UserService;
import com.example.asamurik_rest_api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            String username = jwtUtil.getUsernameFromToken(token);
//            UUID userUuid = UUID.fromString(userID);
            logger.debug("Extracted user UUID: {}", username);

            return userService.findByUsername(username, request);
        }

        return new ResponseHandler().handleResponse(
                ErrorCode.UNAUTHORIZED.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null,
                null,
                request
        );

    }

    @PostMapping("/profile")
    public ResponseEntity<Object> updateUserProfile(
            @RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file, @Valid @RequestBody ValidateUpdateUserDTO updateUserDTO,
            HttpServletRequest request
    ) {
        try {
            if (token != null && jwtUtil.validateToken(token)) {
//            String userID = jwtUtil.getUserIdFromToken(token);
//            UUID userUuid = UUID.fromString(userID);
                String username = jwtUtil.getUsernameFromToken(token);
                logger.debug("Extracted username: {}", username);

                //TODO: Update this to use the correct ID from the JWT claims
                return userService.updateByUsername(username, file, userService.mapToUser(updateUserDTO), request);
            }else{
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
