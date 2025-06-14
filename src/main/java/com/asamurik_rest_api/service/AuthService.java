package com.asamurik_rest_api.service;

import com.asamurik_rest_api.core.IAuth;
import com.asamurik_rest_api.dto.response.OTPResponse;
import com.asamurik_rest_api.dto.response.TokenResponse;
import com.asamurik_rest_api.dto.validation.*;
import com.asamurik_rest_api.entity.User;
import com.asamurik_rest_api.handler.GlobalErrorHandler;
import com.asamurik_rest_api.handler.ResponseHandler;
import com.asamurik_rest_api.repository.UserRepository;
import com.asamurik_rest_api.security.BcryptImpl;
import com.asamurik_rest_api.utils.JwtUtil;
import com.asamurik_rest_api.utils.OtpGenerator;
import com.asamurik_rest_api.utils.RandomTokenUtil;
import com.asamurik_rest_api.utils.SendMailUtil;
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

import java.time.LocalDateTime;
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
        try {
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isPresent() && userOptional.get().getUsername() != null) {
                return GlobalErrorHandler.dataSudahTerdaftar(null, request, "Email");
            }

            if (userRepository.existsByUsername(user.getUsername())) {
                return GlobalErrorHandler.dataSudahTerdaftar(null, request, "Username");
            }

            String otp = OtpGenerator.generateOtp();

            user.setOtp(BcryptImpl.hash(otp));
            user.setPassword(BcryptImpl.hash(user.getPassword()));

            User savedUser = userRepository.save(user);

            savedUser.setUpdatedAt(LocalDateTime.now());
            savedUser.setUpdatedBy(savedUser.getId().toString());

            SendMailUtil.sendOTP(
                    "OTP Verifikasi Registrasi Akun",
                    user.getFullname(),
                    user.getEmail(),
                    otp,
                    "ver_otp.html"
            );

            Thread.sleep(1000);

            return new ResponseHandler().handleResponse(
                    "Registrasi berhasil",
                    HttpStatus.CREATED,
                    mapToOTPResponseDTO(user, otp),
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Registrasi gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> verifyRegis(User user, HttpServletRequest request) {
        try {
            String otp = OtpGenerator.generateOtp();

            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.dataTidakTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();

            if (userDB.isActive()) {
                return GlobalErrorHandler.akunSudahAktif(null, request);
            }

            if (!BcryptImpl.verifyHash(user.getOtp(), userDB.getOtp())) {
                return GlobalErrorHandler.otpSalah(null, request);
            }

            userDB.setActive(true);
            userDB.setUpdatedAt(LocalDateTime.now());
            userDB.setUpdatedBy(userDB.getId().toString());
            userDB.setOtp(BcryptImpl.hash(otp));

            return new ResponseHandler().handleResponse(
                    "Verifikasi registrasi berhasil",
                    HttpStatus.OK,
                    null,
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Verifikasi gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> login(User user, HttpServletRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.usernameAtauPasswordSalah(null, request);
            }

            User userDB = userOptional.get();
            if (!userDB.isActive()) {
                return GlobalErrorHandler.akunBelumAktif(null, userDB.getEmail(), request);
            }

            if (!BcryptImpl.verifyHash(user.getPassword(), userDB.getPassword())) {
                return GlobalErrorHandler.usernameAtauPasswordSalah(null, request);
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userDB.getId());
            claims.put("username", userDB.getUsername());
            claims.put("fullname", userDB.getFullname());
            claims.put("email", userDB.getEmail());
            claims.put("phoneNumber", userDB.getPhoneNumber());

            String token = jwtUtil.doGenerateToken(claims, userDB.getId().toString());

            return new ResponseHandler().handleResponse(
                    "Login berhasil",
                    HttpStatus.OK,
                    mapToTokenResponseDTO(token),
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Login gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> sendOTP(User user, HttpServletRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.dataTidakTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();
            String subject = userDB.isActive()
                    ? "OTP Reset Password"
                    : "OTP Verifikasi Registrasi Akun";

            String otp = OtpGenerator.generateOtp();
            userDB.setOtp(BcryptImpl.hash(otp));
            userDB.setUpdatedAt(LocalDateTime.now());
            userDB.setUpdatedBy(userDB.getId().toString());

            SendMailUtil.sendOTP(subject, userDB.getFullname(), userDB.getEmail(), otp, "ver_otp.html");

            Thread.sleep(1000);

            return new ResponseHandler().handleResponse(
                    "OTP berhasil dikirim ke email anda",
                    HttpStatus.OK,
                    mapToOTPResponseDTO(userDB, otp),
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Pengiriman OTP gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> forgotPassword(String email, HttpServletRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.dataTidakTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();

            if (!userDB.isActive()) {
                return GlobalErrorHandler.akunBelumAktif(null, userDB.getEmail(), request);
            }

            String otp = OtpGenerator.generateOtp();
            userDB.setOtp(BcryptImpl.hash(otp));
            userDB.setUpdatedAt(LocalDateTime.now());
            userDB.setUpdatedBy(userDB.getId().toString());

            SendMailUtil.sendOTP(
                    "OTP Reset Password",
                    userDB.getFullname(),
                    userDB.getEmail(),
                    otp,
                    "ver_otp.html"
            );

            Thread.sleep(1000);

            return new ResponseHandler().handleResponse(
                    "OTP berhasil dikirim ke email anda",
                    HttpStatus.OK,
                    mapToOTPResponseDTO(userDB, otp),
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "OTP gagal dikirim, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> verifyForgotPassword(User user, HttpServletRequest request) {
        try {
            String token = RandomTokenUtil.doGenerateToken();
            String otp = OtpGenerator.generateOtp();

            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.dataTidakTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();
            if (!userDB.isActive()) {
                return GlobalErrorHandler.akunBelumAktif(null, userDB.getEmail(), request);
            }

            if (!BcryptImpl.verifyHash(user.getOtp(), userDB.getOtp())) {
                return GlobalErrorHandler.otpSalah(null, request);
            }

            userDB.setOtp(BcryptImpl.hash(otp));
            userDB.setToken(BcryptImpl.hash(token));
            userDB.setUpdatedAt(LocalDateTime.now());
            userDB.setUpdatedBy(userDB.getId().toString());

            return new ResponseHandler().handleResponse(
                    "Verifikasi OTP berhasil, silahkan gunakan token ini untuk reset password",
                    HttpStatus.OK,
                    mapToTokenResponseDTO(token),
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Verifikasi gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    @Override
    public ResponseEntity<Object> resetPassword(User user, HttpServletRequest request) {
        try {
            String token = RandomTokenUtil.doGenerateToken();

            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.dataTidakTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();

            if (!userDB.isActive()) {
                return GlobalErrorHandler.akunBelumAktif(null, userDB.getEmail(), request);
            }

            if (!BcryptImpl.verifyHash(user.getToken(), userDB.getToken())) {
                return GlobalErrorHandler.tokenSalah(null, request);
            }

            userDB.setPassword(BcryptImpl.hash(user.getPassword()));
            userDB.setToken(BcryptImpl.hash(token));
            userDB.setUpdatedAt(LocalDateTime.now());
            userDB.setUpdatedBy(userDB.getId().toString());

            return new ResponseHandler().handleResponse(
                    "Reset password berhasil",
                    HttpStatus.OK,
                    null,
                    null,
                    request
            );
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "Reset password gagal, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }
    }

    public User mapToUser(RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, User.class);
    }

    public User mapToUser(VerifyOneTimePasswordDTO verifyOneTimePasswordDTO) {
        return modelMapper.map(verifyOneTimePasswordDTO, User.class);
    }

    public User mapToUser(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, User.class);
    }

    public User mapToUser(EmailDTO emailDTO) {
        return modelMapper.map(emailDTO, User.class);
    }

    public User mapToUser(ResetPasswordDTO resetPasswordDTO) {
        return modelMapper.map(resetPasswordDTO, User.class);
    }

    public OTPResponse mapToOTPResponseDTO(User user, String otp) {
        OTPResponse otpResponse = new OTPResponse();
        otpResponse.setEmail(user.getEmail());
        otpResponse.setOTP(otp);
        return otpResponse;
    }

    public TokenResponse mapToTokenResponseDTO(String token) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(token);
        return tokenResponse;
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
