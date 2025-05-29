package com.asamurik_rest_api.service;

import com.asamurik_rest_api.config.OtherConfig;
import com.asamurik_rest_api.core.IAuth;
import com.asamurik_rest_api.dto.validation.LoginDTO;
import com.asamurik_rest_api.dto.validation.RegistrationDTO;
import com.asamurik_rest_api.dto.validation.VerifyOneTimePasswordDTO;
import com.asamurik_rest_api.entity.User;
import com.asamurik_rest_api.handler.GlobalErrorHandler;
import com.asamurik_rest_api.handler.ResponseHandler;
import com.asamurik_rest_api.repository.UserRepository;
import com.asamurik_rest_api.security.BcryptImpl;
import com.asamurik_rest_api.utils.JwtUtil;
import com.asamurik_rest_api.utils.OtpGenerator;
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
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (userOptional.isPresent() && userOptional.get().getUsername() != null) {
                return GlobalErrorHandler.dataSudahTerdaftar(null, request, "Email");
            }

            if (userRepository.existsByUsername(user.getUsername())) {
                return GlobalErrorHandler.dataSudahTerdaftar(null, request, "Username");
            }

            String otp = OtpGenerator.generateOtp();

            user.setOtp(otp);
            user.setPassword(BcryptImpl.hash(user.getPassword()));

            userRepository.save(user);

            if (OtherConfig.getEnableAutomationTesting().equals("y")) {
                data.put("otp", otp);
            }

            SendMailUtil.sendOTP(
                    "OTP Verifikasi Registrasi Akun",
                    user.getFullname(),
                    user.getEmail(),
                    otp,
                    "ver_otp.html"
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
                return GlobalErrorHandler.dataSudahTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();

            if (userDB.isActive()) {
                return GlobalErrorHandler.akunSudahAktif(null, request);
            }

            if (!user.getOtp().equals(userDB.getOtp())) {
                return GlobalErrorHandler.otpSalah(null, request);
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
                return GlobalErrorHandler.usernameAtauPasswordSalah(null, request);
            }

            userDB = userOptional.get();
            if (!BcryptImpl.verifyHash(user.getPassword(), userDB.getPassword())) {
                return GlobalErrorHandler.usernameAtauPasswordSalah(null, request);
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
    public ResponseEntity<Object> forgotPassword(String email, HttpServletRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return GlobalErrorHandler.dataTidakTerdaftar(null, request, "Email");
            }

            User userDB = userOptional.get();

            if (!userDB.isActive()) {
                return GlobalErrorHandler.akunBelumAktif(null, request);
            }

            String otp = OtpGenerator.generateOtp();
            userDB.setOtp(otp);
            if (OtherConfig.getEnableAutomationTesting().equals("y")) {
                data.put("otp", otp);
            }

            SendMailUtil.sendOTP(
                    "OTP Reset Password",
                    userDB.getFullname(),
                    userDB.getEmail(),
                    otp,
                    "ver_otp.html"
            );

            data.put("email", userDB.getEmail());

            Thread.sleep(1000);
        } catch (Exception e) {
            return new ResponseHandler().handleResponse(
                    "OTP gagal dikirim, server sedang gangguan, silahkan coba lagi nanti",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null,
                    request
            );
        }

        return new ResponseHandler().handleResponse(
                "OTP berhasil dikirim ke email anda",
                HttpStatus.OK,
                data,
                null,
                request
        );
    }

    @Override
    public ResponseEntity<Object> resetPassword(User user, HttpServletRequest request) {
        return null;
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
