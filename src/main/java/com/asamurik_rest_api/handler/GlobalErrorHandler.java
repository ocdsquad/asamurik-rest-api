package com.asamurik_rest_api.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class GlobalErrorHandler {


    public static ResponseEntity<Object> dataGagalDisimpan(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("gagal menyimpan", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataGagalDiubah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("data gagal diubah", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataGagalDihapus(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("data gagal dihapus", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> terjadiKesalahan(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("terjadi kesalahan", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataTidakDitemukan(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("data tidak ditemukan", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> objectIsNull(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("object null", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataSudahTerdaftar(String errorCode, HttpServletRequest request, String name) {
        return new ResponseHandler().handleResponse(name + " sudah terdaftar", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataTidakTerdaftar(String errorCode, HttpServletRequest request, String name) {
        return new ResponseHandler().handleResponse(name + " tidak terdaftar", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> otpSalah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("OTP salah", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> akunSudahAktif(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("akun sudah aktif", HttpStatus.CONFLICT, null, errorCode, request);
    }

    public static ResponseEntity<Object> akunBelumAktif(String errorCode, String email, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("akun belum aktif", HttpStatus.FORBIDDEN, Map.of("email", email), errorCode, request);
    }

    public static ResponseEntity<Object> usernameAtauPasswordSalah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("username atau password salah", HttpStatus.UNAUTHORIZED, null, errorCode, request);
    }

    public static ResponseEntity<Object> tokenSalah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("token yang anda masukkan salah", HttpStatus.UNAUTHORIZED, null, errorCode, request);
    }
    public static ResponseEntity<Object> typeImageSalah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("type image yang anda masukkan salah, silahkan masukkan file bertipe jpg,jpeg,png", HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, errorCode, request);
    }
}
