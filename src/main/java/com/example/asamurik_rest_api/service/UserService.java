package com.example.asamurik_rest_api.service;

import com.example.asamurik_rest_api.common.response.SuccessCode;
import com.example.asamurik_rest_api.core.IService;
import com.example.asamurik_rest_api.dto.response.UserProfileResponse;
import com.example.asamurik_rest_api.dto.validation.ValidateUpdateUserDTO;
import com.example.asamurik_rest_api.entity.User;
import com.example.asamurik_rest_api.handler.GlobalErrorHandler;
import com.example.asamurik_rest_api.handler.ResponseHandler;
import com.example.asamurik_rest_api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class UserService implements IService<User, UUID> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        Optional<User> existingUserByUsername = userRepository.findByUsername(id.toString());
        if (existingUserByUsername.isPresent()) {
            User updatedUser = existingUserByUsername.get();
            // Update the user fields as necessary
            updatedUser.setUsername(user.getFullname());
            userRepository.save(updatedUser);
            return new ResponseHandler().handleResponse(SuccessCode.UPDATE_USER_SUCCESS.getMessage(), HttpStatus.OK, null, null, request);
        }
        return null;
    }public ResponseEntity<Object> updateByUsername(String username, User user, HttpServletRequest request) {
        // Check if the user exists
//        Optional<User> existingUser = userRepository.findById(id);
    // diganti nanti kalo claims nya udah diubah jadi id bukan username
        //TODO: Update this to use the correct ID from the JWT claims
        //TODO: Bikin logic buat upload foto profile
        Optional<User> existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername.isPresent()) {
            User updatedUser = existingUserByUsername.get();
            // Update the user fields as necessary
            updatedUser.setFullname(user.getFullname());
            userRepository.save(updatedUser);
            return new ResponseHandler().handleResponse(SuccessCode.UPDATE_USER_SUCCESS.getMessage(), HttpStatus.OK, null, null, request);
        }
        return null;
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
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            // Map the User entity to UserProfileResponse DTO
            UserProfileResponse userProfileResponse = mapToUserProfileResponse(user.get());
            return new ResponseHandler().handleResponse(SuccessCode.GET_USER_SUCCESS.getMessage(), HttpStatus.OK, userProfileResponse, null, request);
        }
        return GlobalErrorHandler.dataTidakDitemukan(null, request);
    }


    @Override
    public ResponseEntity<Object> findByParam(Pageable pageable, String columnName, String value, HttpServletRequest request) {
        return null;
    }

    public ResponseEntity<Object> findByUsername(String username, HttpServletRequest request) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            UserProfileResponse userProfileResponse = mapToUserProfileResponse(user.get());
            return new ResponseHandler().handleResponse(SuccessCode.GET_USER_SUCCESS.getMessage(), HttpStatus.OK, userProfileResponse, null, request);
        }
        return GlobalErrorHandler.dataTidakDitemukan(null, request);
    }

    public User mapToUser(ValidateUpdateUserDTO updateUserDTO) {
        return modelMapper.map(updateUserDTO, User.class);
    }

    public UserProfileResponse mapToUserProfileResponse(User user) {
        return modelMapper.map(user, UserProfileResponse.class);
    }


}
