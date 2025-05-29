package com.example.asamurik_rest_api.service;

import com.example.asamurik_rest_api.common.response.SuccessCode;
import com.example.asamurik_rest_api.core.IService;
import com.example.asamurik_rest_api.dto.response.UserProfileResponse;
import com.example.asamurik_rest_api.dto.validation.ValidateUpdateUserDTO;
import com.example.asamurik_rest_api.entity.User;
import com.example.asamurik_rest_api.handler.GlobalErrorHandler;
import com.example.asamurik_rest_api.handler.ResponseHandler;
import com.example.asamurik_rest_api.repository.UserRepository;
import com.example.asamurik_rest_api.utils.FileStorageUtil;
import com.example.asamurik_rest_api.utils.FileValidatorUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class UserService implements IService<User, UUID> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public ResponseEntity<Object> save(User user, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> update(UUID id, User user, HttpServletRequest request) {
        // Check if the user exists
//        Optional<User> existingUser = userRepository.findById(id);
        // diganti nanti kalo claims nya udah diubah jadi id bukan username
        //TODO: Update this to use the correct ID from the JWT claims
        //TODO: Bikin logic buat upload foto profile
        try {
            Optional<User> existingUserByUsername = userRepository.findByUsername(id.toString());
            if (existingUserByUsername.isPresent()) {
                User updatedUser = existingUserByUsername.get();
                // Update the user fields as necessary
                updatedUser.setUsername(user.getFullname());
                userRepository.save(updatedUser);
                return new ResponseHandler().handleResponse(SuccessCode.UPDATE_USER_SUCCESS.getMessage(), HttpStatus.OK, null, null, request);
            } else {
                return GlobalErrorHandler.dataTidakDitemukan(null, request);
            }
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Error updating user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }

    }

    public ResponseEntity<Object> updateByUsername(String username, MultipartFile file, User user, HttpServletRequest request) {
        // Check if the user exists
//        Optional<User> existingUser = userRepository.findById(id);
        // diganti nanti kalo claims nya udah diubah jadi id bukan username
        //TODO: Update this to use the correct ID from the JWT claims
        //TODO: Bikin logic buat upload foto profile
        try {
            Optional<User> existingUserByUsername = userRepository.findByUsername(username);
            long maxSize = 5 * 1024 * 1024; // 5 MB

            if (existingUserByUsername.isPresent()) {
                User updatedUser = existingUserByUsername.get();
                // Update the user fields as necessary
                if (user.getFullname() != null && !user.getFullname().isEmpty()) {
                    logger.debug("Updating user fullname to: {}", user.getFullname());
                    updatedUser.setFullname(user.getFullname());
                }
                if (file != null) {
                    if (FileValidatorUtil.isImageFile(file) && FileValidatorUtil.isValidFileSize(file.getSize(), maxSize)) {
                        String imageUrl = FileStorageUtil.saveFile(file, "uploads/profile_images/");
                        logger.debug("Image URL: {}", imageUrl);
                        updatedUser.setImageUrl(imageUrl);
                    } else {
                        return new ResponseHandler().handleResponse(
                                "Invalid file type or size exceeds 5MB",
                                HttpStatus.BAD_REQUEST,
                                null,
                                null,
                                request
                        );
                    }

                }

                userRepository.save(updatedUser);
                return new ResponseHandler().handleResponse(SuccessCode.UPDATE_USER_SUCCESS.getMessage(), HttpStatus.OK, null, null, request);
            } else {
                return GlobalErrorHandler.dataTidakDitemukan(null, request);
            }
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Error updating user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> delete(UUID id, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findById(UUID id, HttpServletRequest request) {
        // Check if the user exists
        try {
            id = UUID.fromString(id.toString());
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                // Map the User entity to UserProfileResponse DTO
                UserProfileResponse userProfileResponse = mapToUserProfileResponse(user.get());
                return new ResponseHandler().handleResponse(SuccessCode.GET_USER_SUCCESS.getMessage(), HttpStatus.OK, userProfileResponse, null, request);
            } else {
                return GlobalErrorHandler.dataTidakDitemukan(null, request);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseHandler().handleResponse(
                    "Invalid UUID format: " + id,
                    HttpStatus.BAD_REQUEST,
                    null,
                    null,
                    request
            );
        }

    }

    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, String columnName, String value, HttpServletRequest request) {
        return null;
    }

    public ResponseEntity<Object> findByUsername(String username, HttpServletRequest request) {
        try {


            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                UserProfileResponse userProfileResponse = mapToUserProfileResponse(user.get());
                return new ResponseHandler().handleResponse(SuccessCode.GET_USER_SUCCESS.getMessage(), HttpStatus.OK, userProfileResponse, null, request);
            } else {
                return GlobalErrorHandler.dataTidakDitemukan(null, request);
            }
        } catch (IllegalArgumentException e) {
            // Handle the case where the username is not a valid UUID
            return new ResponseHandler().handleResponse(
                    "Invalid UUID format: " + username,
                    HttpStatus.BAD_REQUEST,
                    null,
                    null,
                    request
            );
        }
    }

    public User mapToUser(ValidateUpdateUserDTO updateUserDTO) {
        return modelMapper.map(updateUserDTO, User.class);
    }

    public UserProfileResponse mapToUserProfileResponse(User user) {
        return modelMapper.map(user, UserProfileResponse.class);
    }


}
