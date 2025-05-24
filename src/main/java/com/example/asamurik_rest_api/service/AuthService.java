package com.example.asamurik_rest_api.service;

import com.example.asamurik_rest_api.core.IAuth;
import com.example.asamurik_rest_api.dto.validation.RegistrationDTO;
import com.example.asamurik_rest_api.entity.User;
import com.example.asamurik_rest_api.handler.ResponseHandler;
import com.example.asamurik_rest_api.repository.UserRepository;
import com.example.asamurik_rest_api.security.BcryptImpl;
import com.example.asamurik_rest_api.util.SendMailUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class AuthService implements IAuth<User> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final Random random = new Random();

    @Override
    public ResponseEntity<Object> register(User user, HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                return new ResponseHandler().handleResponse(
                        "Email sudah terdaftar",
                        HttpStatus.CONFLICT,
                        null,
                        null,
                        request
                );
            }

            if (userRepository.existsByUsername(user.getUsername())) {
                return new ResponseHandler().handleResponse(
                        "Username sudah terdaftar",
                        HttpStatus.CONFLICT,
                        null,
                        null,
                        request
                );
            }

            int otp = random.nextInt(999999);

            user.setOtp(BcryptImpl.hash(String.valueOf(otp)));
            user.setPassword(BcryptImpl.hash(user.getPassword()));

            userRepository.save(user);

            SendMailUtil.sendOTP(
                    "OTP Verifikasi Registrasi Akun",
                    user.getFullname(),
                    user.getEmail(),
                    String.valueOf(otp),
                    "ver_regis.html"
            );

            data.put("email", user.getEmail());

            Thread.sleep(1000);
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Registrasi gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }

        return new ResponseHandler().handleResponse(
                "Registrasi berhasil",
                HttpStatus.CREATED,
                data,
                null,
                request
        );
    }

    @Override
    public ResponseEntity<Object> verifyRegis(User user, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> login(User user, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> sendOTP(User user, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> forgotPassword(User user, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> resetPassword(User user, HttpServletRequest request) {
        return null;
    }

    public User mapToUser(RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, User.class);
    }
}
