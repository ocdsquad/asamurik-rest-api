package com.asamurik_rest_api.service;

import com.asamurik_rest_api.common.response.SuccessCode;
import com.asamurik_rest_api.core.IService;
import com.asamurik_rest_api.dto.response.UserProfileResponse;
import com.asamurik_rest_api.dto.validation.ValidateUpdateUserDTO;
import com.asamurik_rest_api.entity.User;
import com.asamurik_rest_api.handler.GlobalErrorHandler;
import com.asamurik_rest_api.handler.ResponseHandler;
import com.asamurik_rest_api.repository.UserRepository;
import com.asamurik_rest_api.security.BcryptImpl;
import com.asamurik_rest_api.utils.FileValidatorUtil;
import com.asamurik_rest_api.utils.OtpGenerator;
import com.asamurik_rest_api.utils.SendMailUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class UserService implements IService<User, UUID> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Cloudinary cloudinary;

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

    public ResponseEntity<Object> updateById(UUID userId, MultipartFile file, User user, HttpServletRequest request) {
        //TODO: Update this to use the correct ID from the JWT claims
        //TODO: Bikin logic buat upload foto profile
        try {
            Optional<User> existingUser = userRepository.findById(userId);
            long maxSize = 5 * 1024 * 1024; // 5 MB

            if (existingUser.isPresent()) {
                User updatedUser = existingUser.get();

                // Update the user fields as necessary
                logger.debug("Updating user fullname to: {}", user.getFullname());
                updatedUser.setFullname(user.getFullname());
                logger.debug("Updating user phone number to: {}", user.getPhoneNumber());
                updatedUser.setPhoneNumber(user.getPhoneNumber());

                if (!updatedUser.getEmail().equals(user.getEmail())) {
                    // Check if the email already exists
                    Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
                    if (existingUserByEmail.isPresent()) {
                        return GlobalErrorHandler.dataSudahTerdaftar(null, request, "Email");
                    }

                    updatedUser.setActive(false);
                    String otp = OtpGenerator.generateOtp();
                    updatedUser.setOtp(BcryptImpl.hash(otp));

                    SendMailUtil.sendOTP(
                            "OTP verifikasi Email",
                            user.getFullname(),
                            user.getEmail(),
                            otp,
                            "ver_otp.html"
                    );

                    Thread.sleep(1000);
                }

                logger.debug("Updating user email to: {}", user.getEmail());
                updatedUser.setEmail(user.getEmail());

                if (user.getImageUrl() == null) {
                    if (file == null) {
                        deleteProfileImage(updatedUser);
//                    FileStorageUtil.deleteFile(updatedUser.getImageUrl()); // Delete the old image if it exists
                        updatedUser.setImageUrl(null);
                    } else {
                        logger.debug("Received file: {}", file.getOriginalFilename());
                        if (!FileValidatorUtil.isImageFile(file)) {
                            logger.debug("Invalid file type: {}", file.getOriginalFilename());
                            return GlobalErrorHandler.typeImageSalah(null, request);
                        }

                        logger.debug("File is valid: {}", file.getOriginalFilename());
                        String imageUrl = uploadProfileImage(file);
//                    String imageUrl = FileStorageUtil.saveFile(file, "uploads/profile_images/");
//                    logger.debug("Image URL: {}", imageUrl);
                        deleteProfileImage(updatedUser); // Delete the old image if it exists
//                    FileStorageUtil.deleteFile(updatedUser.getImageUrl()); // Delete the old image if it exists
                        updatedUser.setImageUrl(imageUrl);
                    }
                }

                updatedUser.setUpdatedAt(LocalDateTime.now());
                updatedUser.setUpdatedBy(updatedUser.getUsername());

                logger.debug("Saving updated user: {}", updatedUser);
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
                    "format UUID salah: " + id,
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
                logger.debug("User imageUrl: {}", user.get().getImageUrl());
                UserProfileResponse userProfileResponse = mapToUserProfileResponse(user.get());
                return new ResponseHandler().handleResponse(SuccessCode.GET_USER_SUCCESS.getMessage(), HttpStatus.OK, userProfileResponse, null, request);
            } else {
                return GlobalErrorHandler.dataTidakDitemukan(null, request);
            }
        } catch (IllegalArgumentException e) {
            // Handle the case where the username is not a valid UUID
            return new ResponseHandler().handleResponse(
                    "format username salah " + username,
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

    private String uploadProfileImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "public_id", "profile_images/" + UUID.randomUUID(),
                "overwrite", true
        ));
        String imageUrl = uploadResult.get("secure_url").toString();
        logger.debug("Image URL: {}", imageUrl);
        return imageUrl;
    }

    private void deleteProfileImage(User updatedUser) throws IOException {
        if (updatedUser.getImageUrl() != null) {
            Map destroyResult = cloudinary.uploader().destroy(extractPublicId(updatedUser.getImageUrl()), ObjectUtils.emptyMap());
            logger.debug("Image destroyed: {}", destroyResult.get("result"));
        }
    }

    public String extractPublicId(String imageUrl) {
        String base = "/upload/";
        int index = imageUrl.indexOf(base);
        if (index == -1) return null;

        String path = imageUrl.substring(index + base.length());
        if (path.startsWith("v") && path.contains("/")) {
            path = path.substring(path.indexOf("/") + 1);
        }

        int lastDot = path.lastIndexOf('.');
        if (lastDot != -1) {
            path = path.substring(0, lastDot);
        }
        return path;
    }
}
