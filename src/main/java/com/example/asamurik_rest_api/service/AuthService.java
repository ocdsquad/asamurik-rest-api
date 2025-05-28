package com.example.asamurik_rest_api.service;

import com.example.asamurik_rest_api.core.IAuth;
import com.example.asamurik_rest_api.dto.validation.LoginDTO;
import com.example.asamurik_rest_api.dto.validation.RegistrationDTO;
import com.example.asamurik_rest_api.dto.validation.VerifyRegistrationDTO;
import com.example.asamurik_rest_api.entity.User;
import com.example.asamurik_rest_api.handler.ResponseHandler;
import com.example.asamurik_rest_api.repository.UserRepository;
import com.example.asamurik_rest_api.security.BcryptImpl;
import com.example.asamurik_rest_api.utils.JwtUtil;
import com.example.asamurik_rest_api.utils.OtpGenerator;
import com.example.asamurik_rest_api.utils.SendMailUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService implements UserDetailsService, IAuth<User> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

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

            String otp = OtpGenerator.generateOtp();

            user.setOtp(BcryptImpl.hash(otp));
            user.setPassword(BcryptImpl.hash(user.getPassword()));

            userRepository.save(user);

            SendMailUtil.sendOTP(
                    "OTP Verifikasi Registrasi Akun",
                    user.getFullname(),
                    user.getEmail(),
                    otp,
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

//    public ResponseEntity<Object> register(String email, String fullname, HttpServletRequest request) {
//        Map<String, Object> data = new HashMap<>();
//        try {
//
//            String otp = OtpGenerator.generateOtp();
//            User user = userRepository.findByEmail(email).orElseGet(() -> {
//                User newUser = new User();
//                newUser.setEmail(email);
//                newUser.setFullname(fullname);
//                newUser.setCreatedAt(LocalDateTime.now());
//                newUser.setOtp(BcryptImpl.hash(otp));
//                return userRepository.save(newUser);
//            });
//
//            SendMailUtil.sendOTP(
//                    "OTP Verifikasi User",
//                    user.getFullname(),
//                    user.getEmail(),
//                    otp,
//                    null
//            );
//
//            data.put("email", user.getEmail());
//
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            return new ResponseHandler().handleResponse(
//                    "Registrasi gagal, server sedang gangguan, silahkan coba lagi nanti",
//                    HttpStatus.INTERNAL_SERVER_ERROR,
//                    null,
//                    null,
//                    request
//            );
//        }
//
//        return new ResponseHandler().handleResponse(
//                "Registrasi berhasil",
//                HttpStatus.CREATED,
//                data,
//                null,
//                request
//        );
//    }

    @Override
    public ResponseEntity<Object> verifyRegis(User user, HttpServletRequest request) {
        try {
            String otp = OtpGenerator.generateOtp();

            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isEmpty()) {
                return new ResponseHandler().handleResponse(
                        "Email tidak terdaftar",
                        HttpStatus.NOT_FOUND,
                        null,
                        null,
                        request
                );
            }

            User userDB = userOptional.get();

            if (userDB.isActive()) {
                return new ResponseHandler().handleResponse(
                        "Akun sudah terverifikasi",
                        HttpStatus.BAD_REQUEST,
                        null,
                        null,
                        request
                );
            }

            if (!BcryptImpl.verifyHash(user.getOtp(), userDB.getOtp())) {
                return new ResponseHandler().handleResponse(
                        "OTP yang anda masukkan salah",
                        HttpStatus.BAD_REQUEST,
                        null,
                        null,
                        request
                );
            }

            userDB.setActive(true);
            userDB.setUpdatedBy(userDB.getId().toString());
            userDB.setOtp(BcryptImpl.hash(otp));
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Verifikasi gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }

        return new ResponseHandler().handleResponse(
                "Verifikasi berhasil",
                HttpStatus.OK,
                null,
                null,
                request
        );
    }

    @Override
    public ResponseEntity<Object> login(User user, HttpServletRequest request) {
        User userDB;

        try {
            Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
            if (userOptional.isEmpty()) {
                return new ResponseHandler().handleResponse(
                        "Username atau password salah",
                        HttpStatus.UNAUTHORIZED,
                        null,
                        null,
                        request
                );
            }

            userDB = userOptional.get();
            if (!BcryptImpl.verifyHash(user.getPassword(), userDB.getPassword())) {
                return new ResponseHandler().handleResponse(
                        "Username atau password salah",
                        HttpStatus.UNAUTHORIZED,
                        null,
                        null,
                        request
                );
            }
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Login gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDB.getUsername());
        claims.put("fullname", userDB.getFullname());
        claims.put("email", userDB.getEmail());
        claims.put("phoneNumber", userDB.getPhoneNumber());

        String token = jwtUtil.doGenerateToken(claims, userDB.getId().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        return new ResponseHandler().handleResponse(
                "Login berhasil",
                HttpStatus.OK,
                data,
                null,
                request
        );
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

    public User mapToUser(VerifyRegistrationDTO verifyRegistrationDTO) {
        return modelMapper.map(verifyRegistrationDTO, User.class);
    }

    public User mapToUser(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, User.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        User userDB = userOptional.get();

        return new org.springframework.security.core.userdetails.User(
                userDB.getUsername(),
                userDB.getPassword(),
                userDB.isEnabled(),
                userDB.isAccountNonExpired(),
                userDB.isCredentialsNonExpired(),
                userDB.isAccountNonLocked(),
                userDB.getAuthorities()
        );
    }
}
