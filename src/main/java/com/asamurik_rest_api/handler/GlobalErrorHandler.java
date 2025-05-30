package com.asamurik_rest_api.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalErrorHandler {


    public static ResponseEntity<Object> dataGagalDisimpan(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("FAILED TO SAVE !!", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataGagalDiubah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("DATA GAGAL DIUBAH", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataGagalDihapus(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("DATA GAGAL DIHAPUS", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> terjadiKesalahan(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("TERJADI KESALAHAN", HttpStatus.INTERNAL_SERVER_ERROR, null, errorCode, request);
    }

    public static ResponseEntity<Object> dataTidakDitemukan(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("DATA TIDAK DITEMUKAN", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> objectIsNull(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("OBJECT NULL !!", HttpStatus.BAD_REQUEST, null, errorCode, request);
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
        return new ResponseHandler().handleResponse("Akun sudah aktif", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> akunBelumAktif(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("Akun belum aktif", HttpStatus.BAD_REQUEST, null, errorCode, request);
    }

    public static ResponseEntity<Object> usernameAtauPasswordSalah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("Username atau password salah", HttpStatus.UNAUTHORIZED, null, errorCode, request);
    }

    public static ResponseEntity<Object> tokenSalah(String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse("Token yang anda masukkan salah", HttpStatus.UNAUTHORIZED, null, errorCode, request);
    }
}
